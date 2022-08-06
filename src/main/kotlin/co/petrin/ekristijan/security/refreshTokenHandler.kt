package co.petrin.ekristijan.security

import co.petrin.ekristijan.SecurityVerticle
import co.petrin.ekristijan.db.PasswordQueries
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("LoginVerticle.refreshTokenHandler")

/**
 * Takes a refresh token & issues a new access token if it is valid.
 * - 200: done, password was reset (LoginSuccess)
 * - 403: refresh not allowed (session terminated forcefully?)
 */
suspend fun SecurityVerticle.refreshTokenHandler(ctx: RoutingContext) {
    val teacherEmail = ctx.user().subject()

    val teacher = awaitBlocking { PasswordQueries.findByEmail(teacherEmail, jooq) }
    if (teacher == null) {
        LOG.info("Could not refresh tokens, teacher $teacherEmail not found")
        ctx.response().setStatusCode(403).end()
    } else {
        respondWithTokens(teacher, ctx)
    }
}