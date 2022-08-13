import {format} from "date-fns";

export function stripSeconds(time: string | dto.LocalTime | null) {
    if (!time) return '';
    const timeStr = time as string;
    return timeStr.substring(0, timeStr.lastIndexOf(":"));
}

export function date2Time(dateTime: string | dto.OffsetDateTime | null) {
    if (!dateTime) return null;
    const time = new Date(dateTime as string)
    return format(time, "HH:mm")
}