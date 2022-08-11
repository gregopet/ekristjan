package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.schoolId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import java.time.LocalDate

/**
 * Returns the current state of pupils: who had departed and when, who is still here?
 * If classes are given as null, all pupils for teacher's school will be returned.
 * Renders a list of DailyDeparture objects.
 */
suspend fun DepartureVerticle.departureStateHandler(ctx: RoutingContext, classes: Array<String>?) {
    awaitBlocking {
        DepartureQueries.dailyDepartures(ctx.schoolId, LocalDate.now(), classes, jooq).let {
            ctx.json(it)
        }
    }
}