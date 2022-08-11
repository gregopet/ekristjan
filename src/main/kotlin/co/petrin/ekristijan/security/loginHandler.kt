package co.petrin.ekristijan.security

import co.petrin.ekristijan.SecurityVerticle
import co.petrin.ekristijan.db.PasswordQueries
import co.petrin.ekristijan.db.tables.records.TeacherRecord
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

private val LOGIN_ATTEMPT_DURATION_MINUTES = 1
private val LOGIN_PASSWORD_ATTEMPTS_MAX = 5
private val ACCESS_TOKEN_EXPIRY_MINUTES = 2
private val REFRESH_TOKEN_EXPIRY_DAYS = 5
private val LOG = LoggerFactory.getLogger("LoginVerticle.loginHandler")

/**
 * Logs a user in if their password matched and issues JWT tokens for authentication.
 * Input: LoginCommand
 * Responses:
 *  - 200: Ok ()
 *  - 401: Invalid password
 *  - 404: User not found
 *  - 412: User has no password set
 *  - 429: Too many attempts
 */
suspend fun SecurityVerticle.loginHandler(ctx: RoutingContext) {
    val loginCommand = ctx.body().asJsonObject().mapTo(LoginCommand::class.java)
    val user = awaitBlocking { PasswordQueries.passwordAttempt(loginCommand.email, LOGIN_ATTEMPT_DURATION_MINUTES, OffsetDateTime.now(), jooq) }

    when {
        user == null -> {
            LOG.info("User tried to log in with non-existing email ${loginCommand.email}")
            ctx.end(404)
        }
        user.passwordLastAttemptCount > LOGIN_PASSWORD_ATTEMPTS_MAX -> {
            LOG.info("User ${loginCommand.email} exceeded max password attempts")
            ctx.end(429)
        }
        user.passwordHash.isNullOrBlank() -> {
            LOG.info("User ${loginCommand.email} hasn't set a password")
            ctx.end(412)
        }
        else -> {
            passEncoder.verify(user.passwordHash, loginCommand.password.toCharArray()).let { passOk ->
                if (!passOk) {
                    LOG.info("User ${loginCommand.email} tried to log in with an invalid password")
                    ctx.end(401)
                } else {
                    LOG.info("Successful login, issuing JWT tokens for ${loginCommand.email}")
                    respondWithTokens(user, ctx)
                }
            }
        }
    }
}

/** Responds to the request with login & refresh tokens */
suspend fun SecurityVerticle.respondWithTokens(teacher: TeacherRecord, ctx: RoutingContext) {
    awaitBlocking {
        val accessToken = generateAccessToken(teacher, ACCESS_TOKEN_EXPIRY_MINUTES, jwtProvider)
        val refreshToken =
            generateNewRefreshToken(teacher.email, REFRESH_TOKEN_EXPIRY_DAYS, jwtProvider)
        ctx.json(LoginDTO(accessToken, refreshToken))
    }
}

private fun RoutingContext.end(statusCode: Int) = response().setStatusCode(statusCode).end()