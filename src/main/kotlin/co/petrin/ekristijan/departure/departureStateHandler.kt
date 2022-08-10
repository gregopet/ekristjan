package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.schoolId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import java.time.LocalDate

/**
 * Returns the current state of pupils: who had departed and when, who is still here?
 */
suspend fun DepartureVerticle.departureStateHandler(ctx: RoutingContext, classes: Array<String>) {
    awaitBlocking {
        DepartureQueries.dailyDepartures(ctx.schoolId, LocalDate.now(), classes, jooq).let {
            ctx.json(it)
        }
    }
}