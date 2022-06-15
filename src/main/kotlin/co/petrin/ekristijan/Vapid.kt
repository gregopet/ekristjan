package co.petrin.ekristijan

/** A Vapid server key - don't use this one, generate them via https://vapidkeys.com/ */
data class Vapid(
    val subject: String = "mailto: <developer@example.org>",
    val publicKey: String = "BEFi2MSXd8_BZ13nqEn35XggYPUYXssEjniygu6Wf-JqF_hXhc1qA5PT3aPeKOk49nwJwI1ndvHv4HDFk6hnK7I",
    val privateKey: String = "1OIdkA_4uyRO30aZiVuCBxMQwAxq12V079dnDOlmv_0",
)