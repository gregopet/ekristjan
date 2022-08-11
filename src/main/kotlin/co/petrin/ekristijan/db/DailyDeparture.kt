package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.tables.records.DepartureRecord
import co.petrin.ekristijan.dto.Pupil
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

/**
 * An aggregation & strong typization of a daily departure.
 * @property day The day for which this data is relevant
 * @property leavesAlone Is this pupil supposed to leave school on their own today?
 * @property departure If the pupil had already left school for the day, the info for that departure
 * @property departurePlan The departure plans for this pupil today
 * @property summon The most recent summon for the pupil on that day
 */
data class DailyDeparture(
    val pupil: Pupil,
    val day: LocalDate,
    val leavesAlone: Boolean,
    val departurePlan: DeparturePlan,
    val departure: Departure?,
    val summon: Summon?,
) {
    /** Today's departure plans */
    data class DeparturePlan(
        val time: LocalTime?,
        val leavesAlone: Boolean?,
        val remark: String?,
    )

    /** The actual departure */
    data class Departure(
        val time: OffsetDateTime,
        val entireDay: Boolean,
        val remark: String?
    )

    /** The time when this teacher was summoned to the door */
    data class Summon(val id: Int, val teacherName: String, val time: OffsetDateTime)
}



