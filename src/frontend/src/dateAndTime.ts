import {DateTime} from "luxon";

export function stripSeconds(time: string | dto.LocalTime | null) {
    if (!time) return '';

    const timeStr = time as string;
    return DateTime.fromSQL(timeStr).toFormat("H.mm")
}

export function date2Time(dateTime: string | dto.OffsetDateTime | null) {
    if (!dateTime) return null;
    const time = DateTime.fromISO(dateTime as string);
    return time.toFormat("H.mm");
}

/** Takes the given local time and gets today's date at that local time */
export function localTimeToday(time: string | dto.LocalTime): Date {
    return DateTime.fromSQL(time as string).toJSDate()
}