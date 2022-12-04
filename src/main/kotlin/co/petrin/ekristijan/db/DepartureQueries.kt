package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.Tables.*
import co.petrin.ekristijan.dto.Pupil
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.Field
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
import org.slf4j.LoggerFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

object DepartureQueries {

    private val LOG = LoggerFactory.getLogger(DepartureQueries::class.java)

    /** When pupils skip entire days, record them as having left at 7 o'clock */
    private val entireDayLeaveTime = LocalTime.of(7, 0);

    /**
     * Fetches the daily departures for a set of classes. Considers:
     * - what time the pupil ordinarily leaves on this day
     * - is there an extraordinary departure announced for a pupil for that day?
     * - did the pupil already leave today?
     * - what departures are planned today at specific times (and the teacher should send the kids off)?
     */
    fun dailyDepartures(schoolId: Int, day: LocalDate, classes: Array<String>?, trans: DSLContext) = with(PUPIL) {
        val dailyDepartureTime = pupilLeaveField(day)
        val departure = field(row(lastDailyDepartureField(day, PUPIL_ID)))
        val summonTeacher = SUMMON.teacher().NAME
        val mostRecentSummon = field(
            select(field(row(SUMMON.SUMMON_ID, SUMMON.CREATED_AT, summonTeacher)))
                .from(SUMMON)
                .where(
                    SUMMON.PUPIL_ID.eq(PUPIL.PUPIL_ID),
                    trunc(SUMMON.CREATED_AT, DatePart.DAY).cast(SQLDataType.LOCALDATE).eq(day)
                )
                .orderBy(SUMMON.CREATED_AT.desc())
                .limit(1)
        )
        val nameField = GIVEN_NAME.concat(`val`(" "), FAMILY_NAME)

        trans
            .select(
                PUPIL_ID,
                nameField,
                GIVEN_NAME,
                FAMILY_NAME,
                CLAZZ,
                LEAVES_ALONE,
                dailyDepartureTime,
                departure,
                *EXTRAORDINARY_DEPARTURE.fields(),
                mostRecentSummon
            )
            .from(PUPIL)
            .leftJoin(EXTRAORDINARY_DEPARTURE).on(
                PUPIL_ID.eq(EXTRAORDINARY_DEPARTURE.PUPIL_ID),
                EXTRAORDINARY_DEPARTURE.DATE.eq(day),
            )
            .where(
                PUPIL.SCHOOL_ID.eq(schoolId),
                if (classes != null) CLAZZ.eq(any(*classes)) else noCondition(),
            )
            .fetch { rec ->
                val plannedDeparture = if (rec.get(EXTRAORDINARY_DEPARTURE.EXTRAORDINARY_DEPARTURE_ID) != null) {
                    rec.into(EXTRAORDINARY_DEPARTURE)
                } else null
                DailyDeparture(
                    pupil = Pupil(rec.get(PUPIL_ID), rec.get(nameField), rec.get(GIVEN_NAME), rec.get(FAMILY_NAME), rec.get(CLAZZ)),
                    day = day,
                    leavesAlone = rec.get(LEAVES_ALONE),
                    departurePlan = DailyDeparture.DeparturePlan(
                        time = plannedDeparture?.time ?: rec.get(dailyDepartureTime),
                        leavesAlone = plannedDeparture?.leavesAlone ?: rec.get(LEAVES_ALONE),
                        remark = plannedDeparture?.remark,
                    ),
                    departure = rec.get(departure)?.value1()?.let {
                        DailyDeparture.Departure(
                            time = it.get(DEPARTURE.TIME),
                            entireDay = it.get(DEPARTURE.ENTIRE_DAY),
                            remark = it.get(DEPARTURE.REMARK),
                        )
                    },
                    summon = rec.get(mostRecentSummon)?.let { DailyDeparture.Summon(
                        it.get(SUMMON.SUMMON_ID), it.get(SUMMON.teacher().NAME), it.get(SUMMON.CREATED_AT)
                    ) }
                )
            }
    }

    /**
     * Declares that a student will leave school at a certain time, overriding their usual departure time.
     */
    fun declareExtraordinaryDeparture(pupilId: Int, teacherId: Int, day: LocalDate, time: LocalTime, remark: String?, leavesAlone: Boolean?, trans: DSLContext) = with(EXTRAORDINARY_DEPARTURE) {
        trans.insertInto(EXTRAORDINARY_DEPARTURE)
            .set(PUPIL_ID, pupilBelongingToSameSchoolAsTeacher(pupilId, teacherId))
            .set(TEACHER_ID, teacherId)
            .set(DATE, day)
            .set(TIME, time)
            .set(REMARK, remark)
            .set(LEAVES_ALONE, leavesAlone)
            .execute()
    }

    /**
     * Publishes a summon request for a student - the pupil should be sent to the door.
     * @param pupilId The pupil who shall be sent to the door
     * @param teacherId The teacher who sent the pupil to the door
     * @param time The time at which the student was summoned
     * @return The summon's ID or null if the summon could not be issued (e.g. pupil's school doesn't match teacher's school)
     */
    fun summonPupil(pupilId: Int, teacherId: Int, time: OffsetDateTime, trans: DSLContext): Int? = with(SUMMON) {
        // should we have two times in the summon table? the created_at as well as the time the summon was made
        // (perhaps containing the device's local time)? Let's just keep one time for now.
        trans
        .insertInto(SUMMON)
        .set(PUPIL_ID, pupilBelongingToSameSchoolAsTeacher(pupilId, teacherId))
        .set(TEACHER_ID, teacherId)
        .set(CREATED_AT, time)
        .returning(SUMMON_ID)
        .fetchOne(SUMMON_ID)
    }

    /**
     * Records the acknowledgement of a pupil being sent home.
     *
     * Currently the time of departure from school is also set to the exact same date because we currently don't have
     * the physical mechanism to record actual departures, but this could change in the future.
     *
     * @param summonId ID of the notification that the pupil needs to come to the gate
     * @param departure The time of the departure
     * @param teacherId The teacher who inserted this departure
     *
     * @return true if the departure was inserted, false if [pupilId] didn't match [schoolId] (permission problem).
     */
    fun acknowledgePupilSummonAndRecordDeparture(summonId: Int, teacherId: Int, departure: OffsetDateTime, trans: DSLContext): Boolean {
        // Puzzle suggestion: this could probably be achieved in a single query :)

        val callingTeacher = TEACHER.`as`("calling_teacher");
        val summonRecord = trans.select(SUMMON.PUPIL_ID, SUMMON_ACK.TIME)
            .from(SUMMON)
            .join(TEACHER).using(SUMMON.TEACHER_ID)
            .leftJoin(SUMMON_ACK).using(SUMMON.SUMMON_ID)
            .where(
                SUMMON.SUMMON_ID.eq(summonId),
                TEACHER.SCHOOL_ID.eq(
                    select(callingTeacher.SCHOOL_ID).from(callingTeacher).where(callingTeacher.TEACHER_ID.eq(teacherId))
                )
            ).fetchOne()

        if (summonRecord == null) {
            LOG.debug("Summon with ID $summonId for teacher $teacherId not found!")
            return false
        }

        if (summonRecord.get(SUMMON_ACK.TIME) != null) {
            LOG.debug("Summon with ID $summonId was already acknowledged!")
            return false
        }

        val pupilId = summonRecord.get(SUMMON.PUPIL_ID)
        trans.insertInto(SUMMON_ACK)
            .set(SUMMON_ACK.SUMMON_ID, summonId)
            .set(SUMMON_ACK.TEACHER_ID, teacherId)
            .set(SUMMON_ACK.TIME, departure)
            .execute()

        recordDeparture(pupilId, teacherId, departure, false, null, trans)
        return true
    }

    /**
     * Records the departure of a pupil.
     *
     * For departures that last entire day, a 7 o'clock leave time will be recorded to simplify the data model & queries.
     *
     * @return true if the departure was recorded, false otherwise (pupil's school didn't match teacher's school for example).
     */
    fun recordDeparture(pupilId: Int, teacherId: Int, departure: OffsetDateTime, entireDay: Boolean, remark: String?, trans: DSLContext): Boolean = with(DEPARTURE) {
        val recordedLeaveTime = if (!entireDay) {
            departure
        } else {
            departure.toLocalDate().atTime(entireDayLeaveTime).atOffset(departure.offset)
        }
        val rowsAffected = trans.insertInto(DEPARTURE)
            .set(PUPIL_ID, pupilBelongingToSameSchoolAsTeacher(pupilId, teacherId))
            .set(TEACHER_ID, teacherId)
            .set(TIME, recordedLeaveTime)
            .set(REMARK, remark)
            .set(ENTIRE_DAY, entireDay)
            .execute()

        rowsAffected > 0
    }

    /**
     * Cancels all (not already cancelled) departures for [pupilId] on [time]'s day. Checks that the pupil belongs to the
     * same school as teacher!
     * @param teacherId The teacher who cancelled the departures
     */
    fun cancelTodaysDepartures(pupilId: Int, teacherId: Int, time: OffsetDateTime, trans: DSLContext) = with(DEPARTURE) {
        trans
            .update(DEPARTURE)
            .set(CANCELLED_AT, time)
            .set(CANCELLED_BY_TEACHER_ID, teacherId)
            .where(
                PUPIL_ID.eq(pupilId),
                CANCELLED_AT.isNull,
                trunc(TIME, DatePart.DAY).cast(SQLDataType.LOCALDATE).eq(time.toLocalDate()),
                pupilBelongingToSameSchoolAsTeacher(pupilId, teacherId).isNotNull
            )
            .execute()
    }

    /** Returns a pupil_id field that is only non-null if the pupil belongs to the school as teacher */
    private fun pupilBelongingToSameSchoolAsTeacher(pupilId: Int, teacherId: Int): Field<Int> =
        field(
            select(PUPIL.PUPIL_ID).from(PUPIL).where(
                PUPIL.PUPIL_ID.eq(pupilId),
                PUPIL.SCHOOL_ID.eq(
                    select(TEACHER.SCHOOL_ID).from(TEACHER).where(TEACHER.TEACHER_ID.eq(teacherId))
                )
            )
        )

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
                trunc(DEPARTURE.TIME, DatePart.DAY).cast(SQLDataType.LOCALDATE).eq(day),
                DEPARTURE.CANCELLED_AT.isNull,
            )
            .orderBy(DEPARTURE.TIME.desc())
            .limit(1)
        )
    }
}