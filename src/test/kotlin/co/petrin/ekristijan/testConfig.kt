package co.petrin.ekristijan

import co.petrin.ekristijan.email.SmtpConfig
import si.razum.vertx.db.DbConfig

/** A config with sane defaults that can be tweaked in tests */
val testConfig = Config(
    port = 9087,
    pupils = emptyList(),
    absoluteUrl = "http://localhost:9087",
    frontendDistFolder = null,
    vapid = Vapid(subject = "test@example.org", publicKey = "publicKey", privateKey = "privateKey"),
    jwtSymetricPassword = "trla baba lan",
    db = DbConfig(host = "localhost", port = 2345, database = "ekristjan", user = "ekristjan", password = "ekristjan" ),
    smtp = SmtpConfig(host = "localhost", port = 25, username = "username", password = "password", fromAddress = "test@example.org", startTLS = false)
)