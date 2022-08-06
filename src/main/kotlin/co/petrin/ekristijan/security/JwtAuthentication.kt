package co.petrin.ekristijan.security

import io.vertx.core.Vertx
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf

/** Standard JWT claim name for token creation timestamps */
val IAT_UNIQUE_PARAM = "iat"

/** Creates a symmetric JWTAuth provider using the provided [secretKey]. */
fun createJwtProvider(vertx: Vertx, secretKey: String) = JWTAuth.create(vertx, jwtAuthOptionsOf(
    pubSecKeys = listOf(
        PubSecKeyOptions().setAlgorithm("HS256").setBuffer(secretKey)
    )
))
