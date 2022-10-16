package co.petrin.ekristijan.backoffice

import co.petrin.ekristijan.BackofficeVerticle
import co.petrin.ekristijan.db.BackofficeQueries
import co.petrin.ekristijan.db.tables.records.TeacherRecord
import co.petrin.ekristijan.security.schoolId
import io.vertx.ext.web.RoutingContext
import org.jooq.DSLContext

/**
 * Saves a teacher. If teacher's ID is 0, the teacher will be inserted.
 * @return 404 if the teacher could not be found
 */
fun BackofficeVerticle.updateTeacherHandler(ctx: RoutingContext) {
    val updatedTeacher = ctx.body().asJsonObject().mapTo(TeacherDTO::class.java)
    if (updatedTeacher.id > 0) {
        updateTeacher(updatedTeacher, ctx, jooq)
    } else {
        insertTeacher(updatedTeacher, ctx, jooq)
    }
}

private fun updateTeacher(teacher: TeacherDTO, ctx: RoutingContext, jooq: DSLContext) {
    val teacherRecord = BackofficeQueries.getTeacher(teacher.id, ctx.schoolId, jooq)
    if (teacherRecord == null) {
        ctx.response().setStatusCode(404).end()
    } else {
        with(teacherRecord) {
            name = teacher.name
            email = teacher.email
            update()
        }
        ctx.end()
    }
}

private fun insertTeacher(teacher: TeacherDTO, ctx: RoutingContext, jooq: DSLContext) {
    with(TeacherRecord()) {
        schoolId = ctx.schoolId
        name = teacher.name
        email = teacher.email
        attach(jooq.configuration())
        insert()
        ctx.end(teacherId.toString())
    }
}