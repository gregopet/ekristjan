package co.petrin.ekristijan.email

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Configuration of the outgoing email server
 * @property startTLS If null, detection will be automatic; otherwise, sets whether TLS is required or not.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SmtpConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val fromAddress: String,
    val startTLS: Boolean?
)