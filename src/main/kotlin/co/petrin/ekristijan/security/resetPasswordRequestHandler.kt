package co.petrin.ekristijan.security

import co.petrin.ekristijan.SecurityVerticle
import co.petrin.ekristijan.db.PasswordQueries
import io.vertx.ext.mail.MailMessage
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory

private val RESET_TOKEN_EXPIRY_MINUTES = 300
private val LOG = LoggerFactory.getLogger("LoginVerticle.passwordResetRequest")

typealias ResetUrlGenerator = (token: String) -> String

/**
 * Creates a password reset token and sends it via email.
 * Body: ResetPasswordRequestCommand
 * Responses:
 * - 404: user with given email not found
 * - 204: done, email was sent
 *
 * @param resetPath A function that constructs a password reset path
 */
suspend fun SecurityVerticle.passwordResetRequest(ctx: RoutingContext, resetPathProvider: ResetUrlGenerator) {
    // if users start complaining, a rate limiter or CAPTCHA might be in order
    val cmd = ctx.body().asJsonObject().mapTo(ResetPasswordRequestCommand::class.java)

    if (!awaitBlocking { PasswordQueries.emailExists(cmd.email, jooq) }) {
        LOG.info("User with email ${cmd.email} was not found")
        ctx.response().setStatusCode(404).end()
    } else {
        generatePasswordResetToken(cmd.email, RESET_TOKEN_EXPIRY_MINUTES, jwtProvider).also { token ->
            mailClient.sendMail(
                createResetEmail(cmd.email, resetPathProvider(token))
            )
        }
        ctx.response().setStatusCode(204).end()
    }
}

// TODO: this template should be more easily modified, translated, tweaked for specific schools...
private fun SecurityVerticle.createResetEmail(teacherEmail: String, resetUrl: String) = MailMessage().apply {
    from = configuration.smtp.fromAddress
    to = listOf(teacherEmail)
    subject = "eKristjan: menjava gesla"
    text = """
        Spoštovani,
        
        prejeli smo zahtevek za ponastavitev eKristjan gesla.
        
        Če želite ponastaviti geslo, odprite naslednjo povezavo: $resetUrl
        
        Če ponastavitve gesla niste zahtevali vi, vam ni potrebno storiti ničesar.
        
        Lepo vas pozdravlja
        vaša eKristjan ekipa""".trimIndent()
    html = """
        <div dir="ltr">
            Spoštovani,
            <br><br>
            prejeli smo zahtevek za ponastavitev eKristjan gesla.
            <div><br></div>
            <div>
                <font size="4">Za ponastavitev gesla
                    <a href="$resetUrl">kliknite tukaj</a>.
                </font>
                <br><br>
                Če ponastavitve gesla niste zahtevali vi, vam ni potrebno storiti 
                ničesar.
                <br><br>
                Lepo vas pozdravlja
                <br>
                vaša eKristjan ekipa
            </div>   
        </div>
    """.trimIndent()
}