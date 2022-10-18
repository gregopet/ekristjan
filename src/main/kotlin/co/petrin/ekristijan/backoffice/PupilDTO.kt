package co.petrin.ekristijan.backoffice

import java.time.LocalDate
import java.time.LocalTime

data class PupilDTO(
    val id: Int,
    val givenName: String,
    val familyName: String,
    val clazz: String,
    val leavesAlone: Boolean,
    val departure: PupilLeaveDays,
    val plannedDepartures: List<ExtraordinaryDeparture>,
)

data class PupilLeaveDays(
    val monday: LocalTime?,
    val tuesday: LocalTime?,
    val wednesday: LocalTime?,
    val thursday: LocalTime?,
    val friday: LocalTime?,
)

data class ExtraordinaryDeparture(
    val date: LocalDate,
    val time: LocalTime,
    val leavesAlone: Boolean?,
    val remark: String?,
)