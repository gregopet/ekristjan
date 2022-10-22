package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.db.DeviceQueries
import co.petrin.ekristijan.dto.Pupil
import co.petrin.ekristijan.dto.event.SendPupilEvent
import co.petrin.ekristijan.security.schoolId
import co.petrin.ekristijan.security.teacherId
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.Urgency
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

private val LOG = LoggerFactory.getLogger("DepartureVerticle.summonHandler")

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
            val payload = JsonObject.mapFrom(SendPupilEvent(summonId, pupil.name, pupil.fromClass)).encode()
            DeviceQueries.devicesToNotify(ctx.schoolId, pupil.fromClass, jooq)
            .also { LOG.info("Notifying ${it.size} devices")  }
            .forEach { subscription ->
                LOG.debug("Sending push notification to ${subscription.endpoint}")
                val response = pushService.send(Notification(subscription.subscription(), payload, Urgency.HIGH)).get()
                LOG.trace("Result had status code ${response.statusCode}")
                if (response.statusCode == 410) {
                    DeviceQueries.unregisterDevice(subscription.endpoint, jooq)
                    LOG.debug("Push notification no longer available, removing")
                }
            }
        } else {
            LOG.warn("Teacher ${ctx.teacherId} wanted to summon pupil $pupil from another school")
            ctx.response().setStatusCode(403).send()
        }
    }
}