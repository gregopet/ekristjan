package co.petrin.ekristijan

/** A Vapid server key - don't use this one, generate them via https://vapidkeys.com/ */
data class Vapid(
    val subject: String,
    val publicKey: String,
    val privateKey: String,
)