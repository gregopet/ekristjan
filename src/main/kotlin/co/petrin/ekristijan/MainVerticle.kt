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
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.slf4j.LoggerFactory
import java.security.Security

private val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

class MainVerticle : CoroutineVerticle() {

  init {
    Security.addProvider(BouncyCastleProvider())
    DatabindCodec.mapper().registerModule(KotlinModule.Builder().build())
  }

  val vapid = Vapid()
  val pushService: PushAsyncService = PushAsyncService(vapid.publicKey, vapid.privateKey, vapid.subject)

  /** All push targets we want to send to - indexed by the subscription URL */
  val pushSubscriptions = mutableMapOf<String, SubscribedClient>()

  override suspend fun start() {

    val router = Router.router(vertx)
    router.get("/push/key").handler(this::pushPublicKey)
    router.put("/push/subscribe").handler(BodyHandler.create()).handler(this::subscribe)
    router.post("/pupils/leave").handler(BodyHandler.create()).handler(this::pupilLeaves)
    router.get("/pupils").handler(this::listPupils)
    router.route().handler(StaticHandler.create("src/frontend/dist"))

    vertx
      .createHttpServer()
      .requestHandler(router::handle)
      .listen(8888)
      .await()
  }

  private fun listPupils(ctx: RoutingContext) {
    ctx.json(
      listOf(
        Pupil("Jan Hrušca", "1A"),
        Pupil("Sara Pakalan", "1A"),
        Pupil("Mia Oberstar", "1A"),
        Pupil("Matija Podvin", "1B"),
        Pupil("Manja Kalan", "1B"),
        Pupil("Nejc Modrič", "1B"),
        Pupil("Klara Kompara", "1B"),
      )
    )
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
              val response = pushService.send(Notification(subscription, payload)).get()
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
    ctx.end(vapid.publicKey)
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
}
