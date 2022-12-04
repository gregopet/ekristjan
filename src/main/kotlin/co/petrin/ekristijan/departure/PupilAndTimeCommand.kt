package co.petrin.ekristijan.departure

import java.time.OffsetDateTime

data class PupilAndTimeCommand(
    val pupilId: Int,
    val time: OffsetDateTime,
)