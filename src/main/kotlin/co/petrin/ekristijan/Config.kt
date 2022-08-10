package co.petrin.ekristijan

import co.petrin.ekristijan.dto.Pupil
import co.petrin.ekristijan.email.SmtpConfig
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import si.razum.vertx.db.DbConfig

/**
 * @property port The port on which the API will be listening
 * @property absoluteUrl The canonical URL prefix to this site
 * @property frontendDistFolder Override for the location of the frontend (by default the classpath version will be used)
 * @property db The database connection properties
 * @property jwtSymetricPassword The password used for JWT encryption
 * @property smtpConfig The password sending configuration
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
)