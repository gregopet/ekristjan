package co.petrin.ekristijan

import co.petrin.ekristijan.security.ACCESS_TOKEN_SCOPE
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import nl.martijndwars.webpush.PushAsyncService
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import co.petrin.ekristijan.device.registrationHandler
import co.petrin.ekristijan.departure.departureStateHandler
import co.petrin.ekristijan.departure.summonAckHandler
import co.petrin.ekristijan.departure.summonHandler

private val LOG = LoggerFactory.getLogger(DepartureVerticle::class.java)

class DepartureVerticle(val jooq: DSLContext, val jwtProvider: JWTAuth) : ConfigurableCoroutineVerticle(LOG) {

  /** Service for sending push notifications */
  lateinit var pushService: PushAsyncService

  /** Holds the last valid version of config */
  lateinit var parsedConfig: Config

  /** Creates a subrouter that handles requests */
  fun createSubrouter(): Router {
    val router = Router.router(vertx)
    router.get("/push/key").handler { ctx -> ctx.end(parsedConfig.vapid.publicKey) }

    // All requests below here need to be authenticated!
    router.route().handler(JWTAuthHandler.create(jwtProvider).withScope(ACCESS_TOKEN_SCOPE))
    router.put("/push/subscribe").handler(BodyHandler.create()).coroutineHandler(::registrationHandler)
    router.post("/pupils/leave").handler(BodyHandler.create()).coroutineHandler(::summonHandler)
    router.post("/pupils/left").handler(BodyHandler.create()).coroutineHandler(::summonAckHandler)
    router.get("/pupils/:classes?").coroutineHandler { ctx ->
      departureStateHandler(ctx, ctx.pathParam("classes")?.split(",")?.toTypedArray())
    }

    return router
  }

  override suspend fun readConfiguration(conf: JsonObject, forceStatusOutput: Boolean) {
    LOG.info("Reloading config")
    parsedConfig = conf.mapTo(Config::class.java)
    pushService = PushAsyncService(parsedConfig.vapid.publicKey, parsedConfig.vapid.privateKey, parsedConfig.vapid.subject)
  }
}
