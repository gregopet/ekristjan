const fakestate: Data = {
    classes: new Map([
        ["1A", {
            pupils: [
                { name: "Jan Hrušca" },
                { name: "Sara Pakalan" },
                { name: "Mia Oberstar" },
            ]
        }],
        ["1B", {
            pupils: [
                { name: "Matija Podvin" },
                { name: "Manja Kalan" },
                { name: "Nejc Modrič" }
            ]
        }]
    ])
}

export default fakestate;
export interface Data { classes: Map<string, ClassInternal> };
export interface ClassInternal { pupils: Pupil[] }
export interface Pupil { name: string }

export function removePupil(className: string, pupil: Pupil) {
    const cls = fakestate.classes.get(className);
    if (cls) {
        const idx = cls.pupils.findIndex((pup) => pup.name == pupil.name);
        if (idx >= 0) {
            cls.pupils.splice(idx, 1);
        }
    }
}