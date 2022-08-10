namespace dto {
interface Keys {
    p256dh: string;
    auth: string;
}

interface Subscription {
    endpoint: string;
    keys: Keys;
}

interface PushSubscription {
    endpoint: string;
    fromClasses: string[];
    keys: Keys;
    subscription: Subscription;
}

type PushEventType = "PUPIL_AT_DOOR";

interface PushEvent {
    type: PushEventType;
}

interface SendPupilEvent extends PushEvent {
    fromClass: string;
    name: string;
    type: PushEventType;
}

interface Pupil {
    fromClass: string;
    name: string;
}

interface LoginSuccess {
    accessToken: string;
    refreshToken: string;
}
}