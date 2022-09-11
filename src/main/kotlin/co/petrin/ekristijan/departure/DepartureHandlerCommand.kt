package co.petrin.ekristijan.departure

import java.time.OffsetDateTime

data class DepartureHandlerCommand(
    val pupilId: Int,
    val time: OffsetDateTime,
)