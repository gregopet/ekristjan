package co.petrin.ekristijan.security

/** Users can log in using this command */
data class LoginCommand(val email: String, val password: String)