package co.petrin.ekristijan.security

/** A response to a successful login */
data class LoginDTO(val accessToken: String, val refreshToken: String)