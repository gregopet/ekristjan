import {reactive, computed} from "vue";
import {localTimeToday} from "@/dateAndTime";

/** List of pupils will be kept here */
export const pupils = reactive<dto.DailyDeparture[]>([])

/** List of all classes that pupils can belong to */
export const classes = computed(() => [ ...new Set(pupils.map( p => p.pupil.fromClass )) ])

/** List of all pupils from given class */
export function pupilsFromClass(forClass: string): dto.DailyDeparture[] {
    return pupils.filter( p => p.pupil.fromClass == forClass);
}

/** Determines whether a pupil had already departed today */
export function pupilDeparted(pupil: dto.DailyDeparture): boolean {
    return !!pupil.departure;
}

/** Determines whether a pupil is currently summoned (summon exists, has not departed yet) */
export function pupilIsSummoned(pupil: dto.DailyDeparture): boolean {
    return !!pupil.summon && !pupilDeparted(pupil);
}

/** Does this pupil leave alone today? */
export function pupilLeavesAlone(pupil: dto.DailyDeparture): boolean {
    return pupil.departurePlan.leavesAlone ?? pupil.leavesAlone;
}

/** Determines whether a pupil needs to depart now (time is late enough, can leave alone, has not departed yet) */
export function pupilNeedsToDepart(pupil: dto.DailyDeparture): boolean {
    return !pupilDeparted(pupil) &&
        pupilLeavesAlone(pupil) &&
        !!pupil.departurePlan.time && localTimeToday(pupil.departurePlan.time) > new Date();
}