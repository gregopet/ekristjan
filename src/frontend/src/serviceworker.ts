/// <reference lib="WebWorker" />

import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching'
import { clientsClaim } from 'workbox-core';
import { isSendPupilEvent } from "@/dto";
import { restoreTokens } from "@/serviceworker/credentialStore";
import {
    authorizedFetch,
    EVENT_LOGIN_FAILED,
    EVENT_LOGIN_SUCCESS,
    handleFetch,
    loggedIn,
    updateTokens
} from "@/serviceworker/authorization";
import {messageClients} from "@/serviceworker/messaging";

declare let self: ServiceWorkerGlobalScope

// Web service installation
cleanupOutdatedCaches()
precacheAndRoute(self.__WB_MANIFEST)
self.skipWaiting()
clientsClaim()

// Authorized fetches, logins etc
self.onfetch = handleFetch;

/**
 * Show push notifications that come from other client code (notifications on Android need to be fired from the service worker!)
 * @param msg The message; it has a type and a payload.
 *  The currently supported types:
 *      - notification (send a notification), payload is {title, NotificationOptions}
 *      - get selected classes
 *      - log the user out
 */
self.onmessage = (msg) => {
    if (msg.data?.type === 'notification') {
        const { title, options } = msg.data;
        self.registration.showNotification(title, options);
    }
    if (msg.data?.type === 'loginStatus') {
        msg.ports[0].postMessage(loggedIn());
    }
    if (msg.data?.type === 'logout') {
        console.log("Logging out")
        updateTokens(undefined)
        messageClients(EVENT_LOGIN_FAILED)
    }
}

/** Notification key */
const SENT_TO_DOOR_ACTION = 'SENT';

/** Service worker was installed or updated, carry over the old login */
self.oninstall = async (ev) => {
    console.log("Service worker updated to newer version")
    restoreTokens().then(updateTokens); // If tokens were forgotten during install, re-remember them
}

/** Show push notifications that come via service worker  */
self.onpush = (ev) => {
    const notification = ev.data!.json() as dto.SendPupilEvent
    if (isSendPupilEvent(notification)) {
        let text: string
        let actions: NotificationAction[] = []
        if (loggedIn()) {
            text = `${notification.name}, ${notification.fromClass}`
            actions.push({
                action: SENT_TO_DOOR_ACTION,
                title: "Poslan k vratom",
            })
        } else {
            // no details - let them log in before getting any names
            text = "Učenca je potrebno poslati domov"
        }
        const data = { summonId: notification.summonId }
        self.registration.showNotification(text, { silent: false, renotify: true, tag: text, actions, data });
        messageClients(notification)
    }
}

self.onnotificationclick = ev => {
    switch(ev.action) {
        case SENT_TO_DOOR_ACTION:
            acknowledgeSummon(ev as NotificationEvent)
            break;
        default:
            console.log("Unknown notification action", ev.action);
    }
}

/** Handles the notification action where pupil summon was acknowledged */
function acknowledgeSummon(notificationEvent: NotificationEvent) {
    const data = notificationEvent.notification.data as dto.SendPupilEvent
    notificationEvent.notification.close();
    // retry if fetch fails?
    if (loggedIn()) {
        console.log("Notifying: we have sent", data.name, "to the door")
        const req = new Request("/departures/pupils/left", {
            method: 'POST',
            body: JSON.stringify({summonId: data.summonId})
        })
        authorizedFetch(req).then( resp => console.log("Got response with status code", resp.status))
    } else {
        // nothing to do.. somebody logged out and then chose the user action?
        console.error("Could not notify pupil departure, we are not logged in!")
    }
}