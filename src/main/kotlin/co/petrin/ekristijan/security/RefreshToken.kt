package co.petrin.ekristijan.security
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.ext.auth.jwtOptionsOf

/*
    Refresh tokens work in the same manner as remember-me tokens: they verify that a user is still valid and issue
    another access token. Because they need refreshing less often they can hit the database or require the user to
    repeat their credentials less often than the access token expiration rate. They can also be used to forcibly
    terminate a user session.

    Each time the user requests a new access token, a new refresh token will be issued to them as well. This guarantees
    that as long as they use the app more often than the refresh token's expiration time, their login is still valid.

    The current implementation only checks that the user still exists in the database with the same email before
    extending the refresh token. More requirements need to be agreed upon with schools before we add extra functionality.
*/

val REFRESH_TOKEN_SCOPE = "refresh-token"

/**
 * Generates a new refresh token using the teacher's email address.
 */
fun generateNewRefreshToken(teacherEmail: String, expirationInDays: Int, jwt: JWTAuth): String {
    val props = jwtOptionsOf(
        subject = teacherEmail,
        expiresInMinutes = expirationInDays * 1440,
    )
    return jwt.generateToken(jsonObjectOf(
        "scope" to REFRESH_TOKEN_SCOPE
    ), props)
}