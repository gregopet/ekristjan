package co.petrin.ekristijan.activity

import co.petrin.ekristijan.ActivityVerticle
import co.petrin.ekristijan.db.ActivityQueries
import co.petrin.ekristijan.security.teacherId
import io.vertx.ext.web.RoutingContext

/** Fetches all activities for a pupil (currently only fetches the name!) */
fun ActivityVerticle.forPupil(ctx: RoutingContext, pupilId: Int) {
    val queries = ActivityQueries.getForPupil(pupilId, ctx.teacherId, jooq)
    ctx.json(queries.map{it.name})
}