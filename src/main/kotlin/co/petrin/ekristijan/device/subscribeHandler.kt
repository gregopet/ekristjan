package co.petrin.ekristijan.device

import co.petrin.ekristijan.DepartureVerticle
import co.petrin.ekristijan.db.DeviceQueries
import co.petrin.ekristijan.dto.PushSubscription
import co.petrin.ekristijan.security.teacherId
import io.vertx.core.Context
import io.vertx.core.impl.VertxThread
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitBlocking
import kotlinx.coroutines.*

/** Registers a device */
suspend fun DepartureVerticle.registrationHandler(ctx: RoutingContext) {
    val sub = ctx.body().asJsonObject().mapTo(PushSubscription::class.java)
    ctx.response().setStatusCode(204).end()
    awaitBlocking {
        DeviceQueries.registerOrUpdateDevice(
            ctx.teacherId, sub, ctx.request().getHeader("User-Agent"), jooq
        )
    }
}