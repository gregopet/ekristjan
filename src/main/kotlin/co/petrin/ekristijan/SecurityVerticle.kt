package co.petrin.ekristijan

import co.petrin.ekristijan.email.SmtpConfig
import co.petrin.ekristijan.security.*
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import de.mkammerer.argon2.Argon2Factory
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.StartTLSOptions
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import java.time.Duration

private val LOG = LoggerFactory.getLogger(SecurityVerticle::class.java)

/**
 * Handles logins & similar concerns so that users can get their access tokens (other verticles need only check them).
 */
class SecurityVerticle(val jooq: DSLContext, val jwtProvider: JWTAuth): ConfigurableCoroutineVerticle(LOG) {

    lateinit var configuration: Config
    val passEncoder = Argon2Factory.create()
    lateinit var mailClient: MailClient

    /** The route under which this verticle is routed */
    private lateinit var parentRoute: String

    /**
     * Creates a subrouter that handles requests.
     * @param parentRoute The route under which this router is located (useful for generating URLs)
     */
    fun createSubrouter(parentRoute: String): Router {
        this.parentRoute = parentRoute
        return Router.router(vertx).apply {
            post("/login").handler(smallBodyHandler()).coroutineHandler(::loginHandler)
            post("/refresh-token").handler(smallBodyHandler()).handler(JWTAuthHandler.create(jwtProvider).withScope(REFRESH_TOKEN_SCOPE)).coroutineHandler(::refreshTokenHandler)
            post("/request-password-reset").handler(smallBodyHandler()).coroutineHandler { ctx ->
                passwordResetRequest(ctx) { tokenAsJwt -> "${configuration.absoluteUrl}#/ponastavi-geslo/$tokenAsJwt" }
            }
            val pwResetTokenHandler = JWTAuthHandler.create(jwtProvider).withScope(RESET_PASSWORD_SCOPE)
            get("/check-password-reset").handler(pwResetTokenHandler).handler { ctx -> ctx.end() } // just checks whether the token is valid
            post("/submit-password-reset").handler(smallBodyHandler()).handler(pwResetTokenHandler).coroutineHandler(::passwordResetHandler)
        }
    }

    override suspend fun readConfiguration(conf: JsonObject, forceStatusOutput: Boolean) {
        conf.mapTo(Config::class.java).also { parsed ->
            configuration = parsed
            mailClient = constructEmailClient(parsed.smtp)
            LOG.info("Password reset tokens will be valid for ${configuration.refreshTokenExpiry}")
        }

    }

    private fun constructEmailClient(config: SmtpConfig): MailClient {
        LOG.info("Email client will connect to ${config.host}")
        return MailClient.create(vertx, MailConfig().apply {
            hostname = config.host
            username = config.username
            password = config.password
            port = config.port
            starttls = when(config.startTLS) {
                null -> StartTLSOptions.OPTIONAL
                true -> StartTLSOptions.REQUIRED
                false -> StartTLSOptions.DISABLED
            }
        })
    }

    /** A body handler for small JSON payloads */
    private fun smallBodyHandler() = BodyHandler.create(false).setBodyLimit(1000)
}


fun handleAccessToken401(ctx: RoutingContext) {
    val error = ctx.failure()?.cause?.message?.let { cause -> "error=\"${cause.replace("\"", "\\\"")}\"" } ?: ""
    if (ctx.statusCode() == 401) ctx.response().setStatusCode(401).putHeader("WWW-Authenticate", "Bearer $error").end()
    else ctx.next()
}