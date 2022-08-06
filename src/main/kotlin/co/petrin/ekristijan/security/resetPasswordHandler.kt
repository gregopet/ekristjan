package co.petrin.ekristijan.security

import co.petrin.ekristijan.SecurityVerticle
import co.petrin.ekristijan.db.PasswordQueries
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("LoginVerticle.passwordResetHandler")

/**
 * Resets a password if the correct token was provided.
 * Body: PasswordResetCommand
 * * Responses:
 * - 204: done, password was reset
 * - 401: bad password reset token
 * - 409: password token was already spent
 */
suspend fun SecurityVerticle.passwordResetHandler(ctx: RoutingContext) {
    val command = ctx.body().asJsonObject().mapTo(ResetPasswordCommand::class.java)
    val reset = decodePasswordResetFromUser(ctx.user())
    awaitBlocking {
        val hash = hashPassword(command.password, passEncoder)
        if (PasswordQueries.passwordReset(reset.teacherEmail, hash, reset.uniqueIdentifier, jooq)) {
            LOG.debug("Password reset successful for ${reset.teacherEmail}")
            ctx.response().setStatusCode(204).end()
        } else {
            LOG.info("Attempt to reuse password reset token: ${reset.teacherEmail} / ${reset.uniqueIdentifier}")
            ctx.response().setStatusCode(409).end()
        }
    }
}