package co.petrin.ekristijan.security

import co.petrin.ekristijan.SecurityVerticle
import co.petrin.ekristijan.TestSetup
import co.petrin.ekristijan.dto.LoginSuccess
import co.petrin.ekristijan.testConfig
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.http.httpServerOptionsOf
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import si.razum.vertx.db.ConnectionPool
import kotlin.random.Random.Default.nextInt
import co.petrin.ekristijan.TextFixtures as fixture
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.icegreen.greenmail.util.ServerSetup.PROTOCOL_SMTP
import io.kotest.assertions.timing.eventually
import io.vertx.kotlin.core.json.jsonObjectOf
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class SecuritySpec : FreeSpec({

    // this is a mess - make helper methods to reduce this garbage!
    val vertx = Vertx.vertx()
    val jwt = createJwtProvider(vertx, "pigs in space")
    val postgres = TestSetup.postgresContainer
    val jooq = ConnectionPool.wrapWithJooq(postgres.openConnection(), true)
    val greenmail = GreenMail(ServerSetup(nextInt(30_000, 50_000), null, PROTOCOL_SMTP))
    greenmail.setUser("username", "password")
    greenmail.start()
    ConfigurableCoroutineVerticle.reloadableHoconConfig(vertx, baseConfig = testConfig.copy(
        smtp = testConfig.smtp.copy(port = greenmail.smtp.port),
    ))

    "A security verticle" - {
        val verticle = SecurityVerticle(jooq, jwt)
        vertx.deployVerticle(verticle).await()
        val server = vertx.createHttpServer(httpServerOptionsOf(port = testConfig.port)).requestHandler(
            verticle.createSubrouter("")
        ).listen().await()

        val client = WebClient.create(vertx, webClientOptionsOf(
            defaultPort = server.actualPort(),
            defaultHost = "localhost",
        ))

        "accepts valid logins and issues access & refresh tokens for them" - {
            val login = client
            .post("/login")
            .sendJson(LoginCommand(fixture.teacherEmail, fixture.teacherPassword))
            .await()

            login.statusCode() shouldBe 200
            val (accessToken, refreshToken) = login.bodyAsJsonObject().mapTo(LoginSuccess::class.java)

            "the refresh token can be used to get a new access token" {
                val refresh = client
                .post("/refresh-token")
                .bearerTokenAuthentication(refreshToken)
                .send().await()

                refresh.statusCode() shouldBe 200
                refresh.bodyAsJsonObject().getString("accessToken") shouldNotBe null
            }

            "the access token cannot be used in place of the refresh token" {
                val refresh = client
                    .post("/refresh-token")
                    .bearerTokenAuthentication(accessToken)
                    .send().await()

                refresh.statusCode() shouldBe 403
            }
        }

        "allows a password reset request to be made" - {
            client.post("/request-password-reset").sendJson(ResetPasswordRequestCommand(fixture.teacherEmail)).await().also {
                it.statusCode() shouldBe 204
            }
            eventually {
                greenmail.receivedMessages.size shouldBe 1
            }
            val pwdResetToken = extractTokenFromResetEmail(greenmail.receivedMessages.get(0))

            "using the obtained token we can reset the password" - {
                val pwdChange = client.post("/submit-password-reset").bearerTokenAuthentication(pwdResetToken).sendJson(
                    jsonObjectOf("password" to "very new password")
                ).await()
                pwdChange.statusCode() shouldBe 204

                "we can login using the new password" {
                    val login = client
                        .post("/login")
                        .sendJson(LoginCommand(fixture.teacherEmail, "very new password"))
                        .await()

                    login.statusCode() shouldBe 200
                }
            }
        }
    }
})

// https://gist.github.com/rponte/23eb0d9fa9f73525e2e0c43955b6ed59
private fun getPlaintextMailBody(msg: MimeMessage): String {
    val multipart = msg.content as MimeMultipart
    val plain = (0..1).map { multipart.getBodyPart(it) }.first {
        it.isMimeType("text/plain")
    }
    return plain.content as String
}

// This test will start failing when we change the template, but we'll worry about it later (sorry, later self!!)
private fun extractTokenFromResetEmail(msg: MimeMessage): String {
    val bodyText = getPlaintextMailBody(msg)
    val tokenStartPos = "/submit-password-reset/".let { preToken ->
        bodyText.indexOf(preToken) + preToken.length
    }
    val tokenEndPos = bodyText.indexOfAny(startIndex = tokenStartPos, chars = charArrayOf(' ', '\r', '\n'))
    return bodyText.substring(tokenStartPos, tokenEndPos).trim()
}