package co.petrin.ekristijan.departure

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DepartureQueries
import co.petrin.ekristijan.security.teacherId
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

private val LOG = LoggerFactory.getLogger("DepartureVerticle.summonAckHandler")

/**
 * Acknowledges that pupils were sent to the door.
 */
suspend fun DepartureVerticle.summonAckHandler(ctx: RoutingContext) {
    val payload = ctx.body().asJsonObject().mapTo(SummonAckHandlerCommand::class.java)
    LOG.info("Acknowledgement of summon ${payload.summonId} by teacher ${ctx.teacherId}")
    val success = awaitBlocking {
        DepartureQueries.acknowledgePupilSummonAndRecordDeparture(
            payload.summonId, ctx.teacherId, OffsetDateTime.now(), jooq
        )
    }
    val status = if (success) 204 else 403
    ctx.response().setStatusCode(status).end()
}