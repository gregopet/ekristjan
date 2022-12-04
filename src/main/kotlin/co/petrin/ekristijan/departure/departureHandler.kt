package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.teacherId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("DepartureVerticle.departureHandler")

/** Stores departures that weren't a result of an acknowledgement (e.g. pupils leaving alone). */
suspend fun DepartureVerticle.departureHandler(ctx: RoutingContext) {
    val departure = ctx.body().asJsonObject().mapTo(PupilAndTimeCommand::class.java)

    LOG.info("Teacher ${ctx.teacherId} sent pupil ${departure.pupilId} home at ${departure.time}")
    val success = awaitBlocking {
        DepartureQueries.recordDeparture(
            pupilId = departure.pupilId,
            teacherId = ctx.teacherId,
            departure = departure.time,
            entireDay = false,
            remark = null,
            trans = jooq
        )
    }
    val status = if (success) 204 else 403
    ctx.response().setStatusCode(status).end()
}