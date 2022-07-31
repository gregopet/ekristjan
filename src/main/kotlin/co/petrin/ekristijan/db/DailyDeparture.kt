package co.petrin.ekristijan.db

import co.petrin.ekristijan.db.tables.records.DepartureRecord
import co.petrin.ekristijan.db.tables.records.ExtraordinaryDepartureRecord
import java.time.LocalTime

/** An aggregation & strong typization of a daily departure. */
data class DailyDeparture(
    val pupilId: Int,
    val name: String,
    val clazz: String,
    val leavesAlone: Boolean,
    val usualDeparture: LocalTime?,
    val actualDeparture: DepartureRecord?,
    val plannedDeparture: ExtraordinaryDepartureRecord?
)

