package co.petrin.ekristijan.backoffice

import co.petrin.ekristijan.BackofficeVerticle
import co.petrin.ekristijan.db.BackofficeQueries
import co.petrin.ekristijan.security.schoolId
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext

private val validateJsons = true

fun BackofficeVerticle.getTeachersHandler(ctx: RoutingContext) {
    val teachers = BackofficeQueries.allTeachers(ctx.schoolId, jooq)

    // a little experiment: output direct json from database & validate it against Kotlin DTO (as opposed to mapping
    // the DB results to a JSON object and serializing that).
    // In production, the validator could obviously be turned off for performance reasons.
    if (validateJsons && teachers.value1() != null) {
        JsonArray(teachers.value1()).forEach { (it as JsonObject).mapTo(TeacherDTO::class.java) }
    }
    ctx.response().putHeader("Content-Type", "application/json").end(teachers.value1() ?: "[]")
}