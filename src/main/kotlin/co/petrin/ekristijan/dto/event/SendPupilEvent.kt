package co.petrin.ekristijan.dto.event

/**
 * A pupil needs to be sent to the door.
 * @property name Name of the pupil
 * @property fromClass Class of the pupil
 */
class SendPupilEvent(
    val summonId: Int,
    val name: String,
    val fromClass: String,
): PushEvent {
    override val type = PushEventType.PUPIL_AT_DOOR
}