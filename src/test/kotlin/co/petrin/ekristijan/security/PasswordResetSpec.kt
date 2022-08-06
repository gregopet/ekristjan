package co.petrin.ekristijan.security

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.vertx.core.Vertx
import io.vertx.ext.auth.jwt.JWTAuth
import java.time.Instant
import java.util.*

class PasswordResetSpec : FreeSpec({

    val vertx = Vertx.vertx()
    val jwt = createJwtProvider(vertx, "pigs in space")

    "Password tokens can be generated and verified" {
        val token = generatePasswordResetToken("teacher@example.org", 30, jwt)
        decodePasswordResetToken(token, jwt).let { (email, uniqueToken) ->
            email shouldBe "teacher@example.org"
            uniqueToken shouldNotBe null
        }
    }

    "Password tokens created with wrong credentials will fail in the decode process" {
        val token = generatePasswordResetToken("teacher@example.org", 30, jwt)
        val anotherJwtProvider = createJwtProvider(vertx, "not actual token")
        val exception = shouldThrow<RuntimeException> {
            decodePasswordResetToken(token, anotherJwtProvider)
        }
        exception.message shouldBe "Signature verification failed"
    }
})
