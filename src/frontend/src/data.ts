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