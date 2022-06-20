import {reactive} from "vue";

/** List of pupils will be kept here */
export const pupils = reactive<dto.Pupil[]>([])

export function removePupil(className: string, pupil: dto.Pupil) {
    /*const cls = fakestate.classes.get(className);
    if (cls) {
        const idx = cls.pupils.findIndex((pup) => pup.name == pupil.name);
        if (idx >= 0) {
            cls.pupils.splice(idx, 1);
        }
    }*/
}