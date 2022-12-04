namespace dto {
interface Keys {
    p256dh: string;
    auth: string;
}

interface PushSubscription {
    endpoint: string;
    fromClasses: string[];
    keys: Keys;
}

type PushEventType = "PUPIL_AT_DOOR";

interface PushEvent {
    type: PushEventType;
}

interface SendPupilEvent extends PushEvent {
    fromClass: string;
    name: string;
    summonId: number;
    type: PushEventType;
}

interface Pupil {
    familyName: string;
    fromClass: string;
    givenName: string;
    id: number;
    name: string;
}

interface LoginDTO {
    accessToken: string;
    refreshToken: string;
}

interface TemporalAccessor {
}

interface Temporal extends TemporalAccessor {
}

interface TemporalAdjuster {
}

interface ChronoLocalDate extends Temporal, TemporalAdjuster {
}

interface LocalDate extends Temporal, TemporalAdjuster, ChronoLocalDate {
    year: number;
    month: number;
}

interface ZoneId {
}

interface ZoneOffset extends ZoneId, TemporalAccessor, TemporalAdjuster {
    totalSeconds: number;
    id: string;
}

interface OffsetDateTime extends Temporal, TemporalAdjuster {
    offset: ZoneOffset;
}

interface Departure {
    entireDay: boolean;
    remark: string | null;
    time: OffsetDateTime;
}

interface LocalTime extends Temporal, TemporalAdjuster {
    hour: number;
    minute: number;
    second: number;
    nano: number;
}

interface DeparturePlan {
    leavesAlone: boolean | null;
    remark: string | null;
    time: LocalTime | null;
}

interface Summon {
    id: number;
    teacherName: string;
    time: OffsetDateTime;
}

interface DailyDeparture {
    day: LocalDate;
    departure: Departure | null;
    departurePlan: DeparturePlan;
    leavesAlone: boolean;
    pupil: Pupil;
    summon: Summon | null;
}

interface PupilAndTimeCommand {
    pupilId: number;
    time: OffsetDateTime;
}

interface PupilLeaveDays {
    friday: LocalTime | null;
    monday: LocalTime | null;
    thursday: LocalTime | null;
    tuesday: LocalTime | null;
    wednesday: LocalTime | null;
}

interface ExtraordinaryDeparture {
    date: LocalDate;
    leavesAlone: boolean | null;
    remark: string | null;
    time: LocalTime;
}

interface PupilDTO {
    clazz: string;
    departure: PupilLeaveDays;
    familyName: string;
    givenName: string;
    id: number;
    leavesAlone: boolean;
    plannedDepartures: ExtraordinaryDeparture[];
}

interface TeacherDTO {
    backofficeAccess: boolean;
    email: string;
    enabled: boolean;
    id: number;
    name: string;
}
}