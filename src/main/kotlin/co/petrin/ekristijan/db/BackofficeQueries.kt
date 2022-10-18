package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.*
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import java.time.LocalDate

object BackofficeQueries {

    /**
     * Fetches all pupils we have for a given school.
     * A little experiment: make the DB output JSON directly
     */
    fun allPupils(schoolId: Int, jooq: DSLContext) = with(PUPIL) {
        val plannedDepartures = field(select(
            jsonArrayAgg(
                jsonObject(
                    key("date").value(EXTRAORDINARY_DEPARTURE.DATE),
                    key("time").value(EXTRAORDINARY_DEPARTURE.TIME),
                    key("leavesAlone").value(EXTRAORDINARY_DEPARTURE.LEAVES_ALONE),
                    key("remark").value(EXTRAORDINARY_DEPARTURE.REMARK),
                )
            )
        ).from(EXTRAORDINARY_DEPARTURE)
        .where(
            EXTRAORDINARY_DEPARTURE.PUPIL_ID.eq(PUPIL_ID),
            EXTRAORDINARY_DEPARTURE.DATE.ge(LocalDate.now())
        ))

        jooq.select(
            jsonArrayAgg(
                jsonObject(
                    key("id").value(PUPIL_ID),
                    key("givenName").value(GIVEN_NAME),
                    key("familyName").value(FAMILY_NAME),
                    key("clazz").value(CLAZZ),
                    key("leavesAlone").value(LEAVES_ALONE),
                    key("departure").value(jsonObject(
                        key("monday").value(LEAVE_MON),
                        key("tuesday").value(LEAVE_TUE),
                        key("wednesday").value(LEAVE_WED),
                        key("thursday").value(LEAVE_THU),
                        key("friday").value(LEAVE_FRI),
                    )),
                    key("plannedDepartures").value(coalesce(plannedDepartures, jsonArray()))
                )
            ).cast(String::class.java)
        )
        .from(PUPIL)
        .where(PUPIL.SCHOOL_ID.eq(schoolId))
        .groupBy()
        .fetchOne()!!
    }

    /** Gets the record of a pupil by ID if the pupil belongs to the given school */
    fun getPupil(pupilId: Int, schoolId: Int, jooq: DSLContext) =
        jooq.selectFrom(PUPIL).where(
            PUPIL.PUPIL_ID.eq(pupilId),
            PUPIL.SCHOOL_ID.eq(schoolId)
        )
        .fetchOne()

    /**
     * Fetches all teachers we have for a school.
     * A little experiment: make the DB output JSON directly
     */
    fun allTeachers(schoolId: Int, trans: DSLContext) = with(TEACHER) {
        trans.select(
            jsonArrayAgg(
                jsonObject(
                    key("id").value(TEACHER_ID),
                    key("email").value(EMAIL),
                    key("name").value(NAME),
                    key("enabled").value(ENABLED),
                    key("backofficeAccess").value(BACKOFFICE_ACCESS),
                )
            ).cast(String::class.java)
        )
        .from(TEACHER)
        .where(SCHOOL_ID.eq(schoolId))
        .groupBy()
        .fetchOne()!!
    }

    /** Gets the record of a teacher by ID if the teacher belongs to the given school */
    fun getTeacher(teacherId: Int, schoolId: Int, jooq: DSLContext) =
        jooq.selectFrom(TEACHER).where(
            TEACHER.TEACHER_ID.eq(teacherId),
            TEACHER.SCHOOL_ID.eq(schoolId)
        )
        .fetchOne()
}