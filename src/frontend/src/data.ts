const fakestate: dto.Pupil[] = [
    {"name": "Jan Hrušca", "fromClass": "1A"},
    {"name": "Sara Pakalan", "fromClass": "1A"},
    {"name": "Mia Oberstar", "fromClass": "1A"},
    {"name": "Matija Podvin", "fromClass": "1B"},
    {"name": "Manja Kalan", "fromClass": "1B"},
    {"name": "Nejc Modrič", "fromClass": "1B"},
    {"name": "Klara Kompara", "fromClass": "1B"}
]

export default fakestate;

export function removePupil(className: string, pupil: dto.Pupil) {
    /*const cls = fakestate.classes.get(className);
    if (cls) {
        const idx = cls.pupils.findIndex((pup) => pup.name == pupil.name);
        if (idx >= 0) {
            cls.pupils.splice(idx, 1);
        }
    }*/
}