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
self.onpush = (ev) => {
    const notification = ev.data!.json() as dto.PushEvent
    if (isSendPupilEvent(notification)) {
        const pupil = `${notification.name}, ${notification.fromClass}`
        const actions: NotificationAction[] = [{
            action: SENT_TO_DOOR_ACTION,
            title: "Poslan k vratom",
        }]
        const data = { summonId: notification.summonId }
        self.registration.showNotification(pupil, { silent: false, renotify: true, tag: pupil, actions, data });
        messageClients(notification)
    }
}

/**
 * Fire this action if user confirms the notification.
 */
self.addEventListener(SENT_TO_DOOR_ACTION, (ev: Event) => {
    const notificationEvent = ev as NotificationEvent
    const data = notificationEvent.notification.data as dto.PushEvent
    notificationEvent.notification.close();
})
