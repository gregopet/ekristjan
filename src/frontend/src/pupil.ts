import {localTimeToday} from "@/dateAndTime";

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
        !!pupil.departurePlan.time && localTimeToday(pupil.departurePlan.time) < new Date();
}

/** Request that the pupil come to the door */
export function requestPupilSummon(pupil: dto.Pupil): Request {
    return new Request("/departures/pupils/leave", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(pupil),
        credentials: "include",
    })
}

/** Acknowledge that a pupil was sent to the door following a summon */
export function requestPupilSummonAck(summonId: number): Request {
    return new Request("/departures/pupils/left", {
        method: 'POST',
        body: JSON.stringify({summonId})
    });
}

/** Assert that a pupil was sent to the door to leave alone */
export function requestPupilLeaveAlone(departure: dto.PupilAndTimeCommand): Request {
    return new Request("/departures/pupils/leftAlone", {
        method: 'POST',
        body: JSON.stringify(departure)
    })
}

/** Cancels the pupil's existing departures for today */
export function cancelTodaysDepartures(departure: dto.PupilAndTimeCommand): Request {
    return new Request("/departures/pupils/cancelDeparture", {
        method: 'POST',
        body: JSON.stringify(departure),
    })
}