// Type guards for DTO data types

export function isSendPupilEvent(event: dto.PushEvent): event is dto.SendPupilEvent {
    return event?.type == "PUPIL_AT_DOOR"
}