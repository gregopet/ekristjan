package co.petrin.ekristijan

import co.petrin.ekristijan.dto.Pupil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import si.razum.vertx.db.DbConfig

/**
 * @property port The port on which the API will be listening
 * @property pupils The list of pupils and their classes
 * @property frontendDistFolder Override for the location of the frontend (by default the classpath version will be used)
 * @property db The database connection properties
 * @property jwtSymetricPassword The password used for JWT encryption
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Config(
    val port: Int,
    val pupils: List<Pupil>,
    val frontendDistFolder: String? = null,
    val vapid: Vapid,
    val jwtSymetricPassword: String,
    val db: DbConfig,
)