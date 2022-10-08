package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.EXTRAORDINARY_DEPARTURE
import co.petrin.ekristijan.db.Tables.PUPIL
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
                    key("name").value(NAME),
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
}