import type {Pupil} from "@/data";
import mitt from 'mitt';

export const eventBus = mitt();

/** The event interface for when a pupil should be sent to the front door */
export interface SendPupil {
    fromClass: string;
    pupil: Pupil;
}

export function isSendPupil(event: any): event is SendPupil {
    return (event as any).fromClass !== undefined;
}