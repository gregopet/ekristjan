package co.petrin.ekristijan

import co.petrin.ekristijan.activity.forPupil
import co.petrin.ekristijan.security.ACCESS_TOKEN_SCOPE
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.JWTAuthHandler
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import si.razum.vertx.config.ConfigurableCoroutineVerticle

private val LOG = LoggerFactory.getLogger(ActivityVerticle::class.java)

class ActivityVerticle(val jooq: DSLContext, val jwtProvider: JWTAuth): ConfigurableCoroutineVerticle(LOG) {

    fun createSubrouter(): Router = Router.router(vertx).apply {
        route().handler(JWTAuthHandler.create(jwtProvider).withScope(ACCESS_TOKEN_SCOPE)).failureHandler(::handleAccessToken401)
        get("/pupil/:pupilId").blockingHandler { ctx ->
            forPupil(ctx, ctx.pathParam("pupilId").toInt())
        }
    }

    override suspend fun readConfiguration(conf: JsonObject, forceStatusOutput: Boolean) {
        // does nothing for now
    }
}