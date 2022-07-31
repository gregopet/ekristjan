package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.*
import co.petrin.ekristijan.db.tables.records.DepartureRecord
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.Field
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

object DepartureQueries {

    /**
     * Fetches the daily departures for a set of classes. Considers:
     * - what time the pupil ordinarily leaves on this day
     * - is there an extraordinary departure announced for a pupil for that day?
     * - did the pupil already leave today?
     * - what departures are planned today at specific times (and the teacher should send the kids off)?
     */
    fun dailyDepartures(schoolId: Int, day: LocalDate, classes: Array<String>, trans: DSLContext) = with(PUPIL) {
        val dailyDepartureTime = pupilLeaveField(day)
        val departure = field(row(lastDailyDepartureField(day, PUPIL_ID)))

        trans
        .select(
            PUPIL_ID,
            NAME,
            CLAZZ,
            LEAVES_ALONE,
            dailyDepartureTime,
            departure
        )
        .from(PUPIL)
        .leftJoin(EXTRAORDINARY_DEPARTURE).on(
            PUPIL_ID.eq(EXTRAORDINARY_DEPARTURE.PUPIL_ID),
            EXTRAORDINARY_DEPARTURE.DATE.eq(day),
        )
        .where(
            PUPIL.SCHOOL_ID.eq(schoolId),
            CLAZZ.eq(any(*classes)),
        )
        .fetch { rec ->
            DailyDeparture(
                pupilId = rec.get(PUPIL_ID),
                name = rec.get(NAME),
                clazz = rec.get(CLAZZ),
                leavesAlone = rec.get(LEAVES_ALONE),
                usualDeparture = rec.get(dailyDepartureTime),
                actualDeparture = rec.get(departure)?.value1()?.into(DEPARTURE),
                plannedDeparture = rec.into(EXTRAORDINARY_DEPARTURE)
            )
        }
    }

    /**
     * Declares that a student will leave school at a certain time, overriding their usual departure time.
     */
    /*fun declareExtraordinaryDeparture(pupilId: Int, schoolId: Int, teacherId: Int?, day: LocalDate, time: LocalTime, remark: String?, leavesAlone: Boolean?, trans: DSLContext) {

    }*/

    /**
     * Records the acknowledgement of a pupil being sent home. Currently the time of departure from school is also
     * set to the exact same date because we currently don't have the physical mechanism to record actual departures,
     * but this could change in the future.
     *
     * @param pupilId The pupil who departed
     * @param schoolId The school of this pupil (has to match the pupilId, or no departure will be inserted and the function will return `false`)
     * @param departure The time of the departure
     * @param entireDay Was the pupil absent for the entire day (didn't even show up)?
     * @param teacherId The teacher who inserted this departure
     * @param remark If the teacher has any remarks about the nature of this departure, it may be inserted here
     *
     * @return true if the departure was inserted, false if [pupilId] didn't match [schoolId] (permission problem).
     */
    /*fun acknowledgePupilNotification(pupilId: Int, schoolId: Int, departure: OffsetDateTime, entireDay: Boolean, teacherId: Int, remark: String?, trans: DSLContext): Boolean {

    }*/

    /**
     * Determines which field from the pupil table is appropriate for a given day.
     * Working weekends may happen from time to time, we will act as we have no leave time recorded for those days.
     */
    private fun pupilLeaveField(day: LocalDate) = with(PUPIL) {
        when(day.dayOfWeek) {
            DayOfWeek.MONDAY    -> LEAVE_MON
            DayOfWeek.TUESDAY   -> LEAVE_TUE
            DayOfWeek.WEDNESDAY -> LEAVE_WED
            DayOfWeek.THURSDAY  -> LEAVE_THU
            DayOfWeek.FRIDAY    -> LEAVE_FRI
            else                -> `val`(null, LEAVE_MON.type)
        }.`as`("leave_that_day")
    }

    /** Creates a field that selects the most recent departure for a pupil on a given day. */
    private fun lastDailyDepartureField(day: LocalDate, pupilId: Field<Int>) = with(DEPARTURE) {
        field(
            select(DEPARTURE.fieldsRow())
            .from(DEPARTURE)
            .where(
                DEPARTURE.PUPIL_ID.eq(pupilId),
                trunc(DEPARTURE.TIME, DatePart.DAY).cast(SQLDataType.LOCALDATE).eq(day)
            )
            .orderBy(DEPARTURE.TIME.desc())
            .limit(1)
        )
    }
}