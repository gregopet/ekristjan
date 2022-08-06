package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.tables.records.DepartureRecord
import co.petrin.ekristijan.db.tables.records.ExtraordinaryDepartureRecord
import co.petrin.ekristijan.db.tables.records.SummonRecord
import java.time.LocalDate
import java.time.LocalTime

/**
 * An aggregation & strong typization of a daily departure.
 * @property day The day for which this data is relevant
 * @property usualDeparture The time at which the pupil usually leaves school on that day
 * @property actualDeparture If the pupil had already left school for the day, the info for that departure
 * @property plannedDeparture If the pupil has a special departure planned for the day, this departure
 * @property summon The most recent summon for the pupil on that day
 */
data class DailyDeparture(
    val pupilId: Int,
    val name: String,
    val clazz: String,
    val day: LocalDate,
    val leavesAlone: Boolean,
    val usualDeparture: LocalTime?,
    val actualDeparture: DepartureRecord?,
    val plannedDeparture: ExtraordinaryDepartureRecord?,
    val summon: SummonRecord?,
)

