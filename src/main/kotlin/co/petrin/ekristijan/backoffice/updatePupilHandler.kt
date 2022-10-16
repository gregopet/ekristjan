package co.petrin.ekristijan.backoffice

import co.petrin.ekristijan.BackofficeVerticle
import co.petrin.ekristijan.db.BackofficeQueries
import co.petrin.ekristijan.db.tables.records.PupilRecord
import co.petrin.ekristijan.security.schoolId
import io.vertx.ext.web.RoutingContext
import org.jooq.DSLContext

/**
 * Saves a pupil. If pupil's ID is 0, the pupil will be inserted.
 * @return 404 if the pupil could not be found
 */
fun BackofficeVerticle.updatePupilHandler(ctx: RoutingContext) {
    val updatedPupil = ctx.body().asJsonObject().mapTo(PupilDTO::class.java)
    if (updatedPupil.id > 0) {
        updatePupil(updatedPupil, ctx, jooq)
    } else {
        insertPupil(updatedPupil, ctx, jooq)
    }
}

private fun updatePupil(pupil: PupilDTO, ctx: RoutingContext, jooq: DSLContext) {
    val pupilRecord = BackofficeQueries.getPupil(pupil.id, ctx.schoolId, jooq)
    if (pupilRecord == null) {
        ctx.response().setStatusCode(404).end()
    } else {
        with(pupilRecord) {
            updateFromDTO(pupil)
            update()
        }
        ctx.end()
    }
}

private fun insertPupil(pupil: PupilDTO, ctx: RoutingContext, jooq: DSLContext) {
    with(PupilRecord()) {
        schoolId = ctx.schoolId
        updateFromDTO(pupil)
        attach(jooq.configuration())
        insert()
        ctx.end(pupilId.toString())
    }
}

private fun PupilRecord.updateFromDTO(pupil: PupilDTO) {
    name = pupil.name
    clazz = pupil.clazz
    leavesAlone = pupil.leavesAlone
    leaveMon = pupil.departure.monday
    leaveTue = pupil.departure.tuesday
    leaveWed = pupil.departure.wednesday
    leaveThu = pupil.departure.thursday
    leaveFri = pupil.departure.friday
}