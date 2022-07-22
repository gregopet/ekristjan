package co.petrin.ekristijan

import co.petrin.ekristijan.dto.Pupil
import co.petrin.ekristijan.dto.PushSubscription
import co.petrin.ekristijan.dto.event.PushEvent
import co.petrin.ekristijan.dto.event.SendPupilEvent
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.ext.web.handler.sockjs.SockJSSocket
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.PushAsyncService
import nl.martijndwars.webpush.Subscription
import nl.martijndwars.webpush.Urgency
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import si.razum.vertx.config.ConfigurableCoroutineVerticle
import java.security.Security

private val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

class MainVerticle : ConfigurableCoroutineVerticle(LOG) {

  /** Service for sending push notifications */
  lateinit var pushService: PushAsyncService

  /** All push targets we want to send to - indexed by the subscription URL */
  val pushSubscriptions = mutableMapOf<String, SubscribedClient>()

  lateinit var parsedConfig: Config

  override suspend fun start() {
    super.start()

    val router = Router.router(vertx)
    router.get("/push/key").handler(this::pushPublicKey)
    router.put("/push/subscribe").handler(BodyHandler.create()).handler(this::subscribe)
    router.post("/pupils/leave").handler(BodyHandler.create()).handler(this::pupilLeaves)
    router.get("/pupils").handler { ctx -> ctx.json(parsedConfig.pupils) }
    router.route().handler(StaticHandler.create(parsedConfig.frontendDistFolder ?: "src/frontend/dist"))

    LOG.info("Main verticle listening on port ${parsedConfig.port}")

    vertx
      .createHttpServer()
      .requestHandler(router::handle)
      .listen(parsedConfig.port)
      .await()
  }

  private fun pupilLeaves(ctx: RoutingContext) {
    val pupil = ctx.body().asJsonObject().mapTo(Pupil::class.java)
    LOG.info("Pupil ${pupil.name}, ${pupil.fromClass} called to main gate")

    // release request & process the notifications on a background thread
    ctx.end()
    launch {
      try {
        val payload = JsonObject.mapFrom(SendPupilEvent(pupil.name, pupil.fromClass)).encode()
        with(pushSubscriptions.values.iterator()) {
          while (hasNext()) {
            val (subscription, subData) = next()
            if (subData.fromClasses.contains(pupil.fromClass)) {
              LOG.debug("Sending push notification to ${subscription.endpoint}")
              val response = pushService.send(Notification(subscription, payload, Urgency.HIGH)).get()
              LOG.trace("Result had status code ${response.statusCode}")
              if (response.statusCode == 410) {
                remove()
                LOG.debug("Push notification no longer available, removing")
              }
            }
          }
        }
      } catch (t: Throwable) {
        LOG.error("Could not send notification", t)
        ctx.response().setStatusCode(500).end("Could not send notification")
      }
    }
  }

  /** Serve the application's public key so the client can subscribe. */
  private fun pushPublicKey(ctx: RoutingContext) {
    LOG.trace("Serving VAPID public key")
    ctx.end(parsedConfig.vapid.publicKey)
  }

  /** Once client subscribes, it will send its details to us here */
  private fun subscribe(ctx: RoutingContext) {
    val sub = ctx.body().asJsonObject().mapTo(PushSubscription::class.java)

    if (pushSubscriptions.containsKey(sub.endpoint)) {
      LOG.debug("Client updating their subscription to classes ${sub.fromClasses.joinToString()} for user ${sub.keys.auth}")
      pushSubscriptions[sub.endpoint]!!.subscriptionData = sub
    } else {
      LOG.debug("Client subscribing to push notifications for classes ${sub.fromClasses.joinToString()} for user ${sub.keys.auth}")
      pushSubscriptions[sub.endpoint] = SubscribedClient(sub.subscription, sub)
    }
    ctx.end()
  }

  override suspend fun readConfiguration(conf: JsonObject, forceStatusOutput: Boolean) {
    LOG.info("Reloading config")
    parsedConfig = conf.mapTo(Config::class.java)
    pushService = PushAsyncService(parsedConfig.vapid.publicKey, parsedConfig.vapid.privateKey, parsedConfig.vapid.subject)
  }
}
