package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.db.DeviceQueries
import co.petrin.ekristijan.dto.Pupil
import co.petrin.ekristijan.dto.PushSubscription
import co.petrin.ekristijan.dto.event.SendPupilEvent
import co.petrin.ekristijan.security.schoolId
import co.petrin.ekristijan.security.teacherId
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.Urgency
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime

private val LOG = LoggerFactory.getLogger("DepartureVerticle.summonHandler")

/** Let the notifications survive for 1 hour */
private val TIME_TO_LIVE_SECONDS = 60

/**
 * Handles the summoning function - pupil is requested at the door.
 * Records the summon and notifies all devices registered to listen for that class.
 * Returns:
 *  - 403 if the pupil ID doesn't match the teacher's school
 */
suspend fun DepartureVerticle.summonHandler(ctx: RoutingContext) {
    val pupil = ctx.body().asJsonObject().mapTo(Pupil::class.java)

    awaitBlocking {
        val summonId = DepartureQueries.summonPupil(pupil.id, ctx.teacherId, OffsetDateTime.now(), jooq)
        if (summonId != null) {
            LOG.info("Teacher ${ctx.teacherId} summoning pupil $pupil")
            ctx.response().setStatusCode(204).send()

            SendPupilEvent(summonId, pupil.name, pupil.fromClass).let { event ->
                DeviceQueries
                    .devicesToNotify(ctx.schoolId, pupil.fromClass, jooq)
                    .also { LOG.info("Notifying ${it.size} devices")  }
                    .forEach { subscription -> sendEventNotification(subscription, event) }
            }
        } else {
            LOG.warn("Teacher ${ctx.teacherId} wanted to summon pupil $pupil from another school")
            ctx.response().setStatusCode(403).send()
        }
    }
}

/**
 * Sends a push notification about a door event.
 */
private fun DepartureVerticle.sendEventNotification(subscription: PushSubscription, event: SendPupilEvent) {
    LOG.debug("Sending push notification to ${subscription.endpoint}")
    val response = pushService.send(
        subscription.notification(event)
    ).get()
    LOG.trace("Result had status code ${response.statusCode}")
    if (response.statusCode == 410) {
        DeviceQueries.unregisterDevice(subscription.endpoint, jooq)
        LOG.debug("Push notification no longer available, removing")
    }
}

/**
 * Builds a notification. This notification will:
 *
 * - not be delivered unless it is opened within a certain time (TTL)
 * - will have high urgency to avoid do not disturb modes
 * - will prevent stacking of undelivered notifications for same pupil (topic)
 */
private fun PushSubscription.notification(ev: SendPupilEvent): Notification = Notification.builder()
    .endpoint(endpoint)
    .userAuth(keys.auth)
    .userPublicKey(keys.p256dh)
    .urgency(Urgency.HIGH)
    .ttl(TIME_TO_LIVE_SECONDS)
    .payload(JsonObject.mapFrom(ev).encode())
    .topic(ev.notificationTopic)
    .build()

/**
 * Creates a pupil-unique notification topic which will prevent messages for the same student from stacking (only the
 * latest notification will be delivered).
 */
private val SendPupilEvent.notificationTopic get() = URLEncoder.encode("${name}-${fromClass}", StandardCharsets.UTF_8)


