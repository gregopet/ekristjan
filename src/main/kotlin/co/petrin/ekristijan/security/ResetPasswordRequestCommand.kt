package co.petrin.ekristijan.security

/** A command sent to trigger a password reset */
data class ResetPasswordRequestCommand(val email: String)