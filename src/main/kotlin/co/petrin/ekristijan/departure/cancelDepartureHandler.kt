package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.teacherId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("DepartureVerticle.cancelDepartureHandler")

suspend fun DepartureVerticle.cancelDepartureHandler(ctx: RoutingContext) {
    val departure = ctx.body().asJsonObject().mapTo(PupilAndTimeCommand::class.java)
    LOG.info("Teacher ${ctx.teacherId} claims pupil ${departure.pupilId} didn't depart after all!")
    ctx.response().setStatusCode(204).end()
    awaitBlocking {
        DepartureQueries.cancelTodaysDepartures(
            departure.pupilId,
            ctx.teacherId,
            departure.time,
            jooq
        )
    }
}
