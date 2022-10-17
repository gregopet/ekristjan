package co.petrin.ekristijan

import co.petrin.ekristijan.security.ACCESS_TOKEN_SCOPE
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.JWTAuthHandler
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import co.petrin.ekristijan.backoffice.getPupilsHandler
import co.petrin.ekristijan.backoffice.updatePupilHandler
import co.petrin.ekristijan.backoffice.getTeachersHandler
import co.petrin.ekristijan.backoffice.updateTeacherHandler
import co.petrin.ekristijan.security.OFFICE_PERMISSION
import io.vertx.ext.web.handler.BodyHandler

private val LOG = LoggerFactory.getLogger(BackofficeVerticle::class.java)

class BackofficeVerticle(val jooq: DSLContext, val jwtProvider: JWTAuth) : ConfigurableCoroutineVerticle(LOG) {
    /** Holds the last valid version of config */
    lateinit var parsedConfig: Config

    /**
     * Creates a subrouter that handles requests.
     * @param parentRoute The route under which this router is located (useful for generating URLs)
     */
    fun createSubrouter(parentRoute: String): Router = Router.router(vertx).apply {
        // All requests below here need to be authenticated, users need the proper permission!
        route().handler(JWTAuthHandler.create(jwtProvider).withScope(ACCESS_TOKEN_SCOPE)).failureHandler(::handleAccessToken401)
        route().handler { ctx ->
            if (OFFICE_PERMISSION.match(ctx.user())) {
                ctx.next()
            } else {
                ctx.response().setStatusCode(403).end("Permission for backoffice was denied")
            }
        }

        get("/pupils").blockingHandler(::getPupilsHandler)
        post("/pupil").handler(BodyHandler.create()).blockingHandler(::updatePupilHandler)

        get("/teachers").blockingHandler(::getTeachersHandler)
        post("/teacher").handler(BodyHandler.create()).blockingHandler(::updateTeacherHandler)
    }

    override suspend fun readConfiguration(conf: JsonObject, forceStatusOutput: Boolean) {
        LOG.info("Reloading config")
        parsedConfig = conf.mapTo(Config::class.java)
    }
}