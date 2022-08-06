package co.petrin.ekristijan.security

import io.vertx.core.Vertx
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.HttpException
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf

/** Standard JWT claim name for token creation timestamps */
val IAT_UNIQUE_PARAM = "iat"

/** Creates a symmetric JWT authentication provider using the provided [secretKey]. */
fun createJwtProvider(vertx: Vertx, secretKey: String) = JWTAuth.create(vertx, jwtAuthOptionsOf(
    pubSecKeys = listOf(
        PubSecKeyOptions().setAlgorithm("HS256").setBuffer(secretKey)
    )
))

/**
 * Gets the bearer token from the request or returns status 401.
 */
fun RoutingContext.demandBearerToken(): String =
    (request().getHeader("Authorization") ?: "").let { headerValue ->
        if (!headerValue.trim().startsWith("bearer ", ignoreCase = true)) {
            throw HttpException(401, "Missing bearer token")
        }
        headerValue.substring("bearer".length).trim()
    }


