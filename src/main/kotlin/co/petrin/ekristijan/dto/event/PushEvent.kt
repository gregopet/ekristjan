package co.petrin.ekristijan.dto.event

/** An event sent to the application via web push */
interface PushEvent {
    val type: PushEventType
}