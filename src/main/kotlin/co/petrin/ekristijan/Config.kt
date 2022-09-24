package co.petrin.ekristijan

import co.petrin.ekristijan.email.SmtpConfig
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import si.razum.vertx.db.DbConfig
import kotlin.time.Duration

/**
 * @property port The port on which the API will be listening
 * @property absoluteUrl The canonical URL prefix to this site
 * @property frontendDistFolder Override for the location of the frontend (by default the classpath version will be used)
 * @property db The database connection properties
 * @property jwtSymetricPassword The password used for JWT encryption
 * @property smtpConfig The password sending configuration
 * @property refreshTokenExpiry How long is the refresh token valid for, in ISO 8601 format
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Config(
    val port: Int,
    val absoluteUrl: String,
    val frontendDistFolder: String? = null,
    val vapid: Vapid,
    val jwtSymetricPassword: String,
    val db: DbConfig,
    val smtp: SmtpConfig,
    val refreshTokenExpiry: String,
) {
    val refreshTokenExpiryInMinutes = Duration.parse(refreshTokenExpiry).inWholeMinutes.toInt()
}