package co.petrin.ekristijan.dto

/** A response to a successful login */
data class LoginSuccess(val accessToken: String, val refreshToken: String)