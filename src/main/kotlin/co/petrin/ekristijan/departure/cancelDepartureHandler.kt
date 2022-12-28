package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.teacherId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.jooq.impl.DSL
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("DepartureVerticle.cancelDepartureHandler")

/**
 * Cancels both departures AND summons. School wanted to have it this way, we saved one API call. Making the two
 * actions separate should be no problem.
 *
 * Ideally, we should also cancel any sent notifications for teachers who haven't received them yet. However, this has
 * some problems, see https://stackoverflow.com/questions/56877538/cancel-sent-web-push-notifications
 * For now we ignore the problem and just do nothing, especially as the person at door has a few seconds' grace period
 * during which they can cancel the notification from being sent in cases of simple misclicks. Will wait on feedback
 * from teachers.
 */
suspend fun DepartureVerticle.cancelDepartureHandler(ctx: RoutingContext) {
    val departure = ctx.body().asJsonObject().mapTo(PupilAndTimeCommand::class.java)
    LOG.info("Teacher ${ctx.teacherId} claims pupil ${departure.pupilId} didn't depart after all!")
    ctx.response().setStatusCode(204).end()
    awaitBlocking {
        jooq.transactionResult { t ->
            DepartureQueries.cancelTodaysDepartures(
                departure.pupilId,
                ctx.teacherId,
                departure.time,
                DSL.using(t)
            )
            DepartureQueries.cancelTodaysSummons(
                departure.pupilId,
                ctx.teacherId,
                departure.time,
                DSL.using(t)
            )
        }
    }
}
