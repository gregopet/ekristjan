package co.petrin.ekristijan

import co.petrin.ekristijan.dto.Pupil
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * @property port The port on which the API will be listening
 * @property pupils The list of pupils and their classes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Config(
    val port: Int,
    val pupils: List<Pupil>,
    val vapid: Vapid,
)