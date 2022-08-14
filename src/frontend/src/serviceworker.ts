/// <reference lib="WebWorker" />

import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching'
import { clientsClaim } from 'workbox-core';
import { isSendPupilEvent } from "@/dto";
import {eventBus} from "@/events";

declare let self: ServiceWorkerGlobalScope

// Web service installation
cleanupOutdatedCaches()
precacheAndRoute(self.__WB_MANIFEST)
self.skipWaiting()
clientsClaim()


/**
 * Messages all clients of this service worker.
 * @param message The message to send
 */
async function messageClients(message: any) {
    const clients = await self.clients.matchAll({ includeUncontrolled: true, type: "window"})
    for (const client of clients) {
        client.postMessage(message);
    }
}

/**
 * Show push notifications that come from other client code (notifications on Android need to be fired from the service worker!)
 * @param msg The message; it has a type and a payload.
 *  The currently supported types:
 *      - notification (send a notification), payload is {title, NotificationOptions}
 *      - get selected classes
 */
self.onmessage = (msg) => {
    if (msg.data?.type === 'notification') {
        const { title, options } = msg.data;
        self.registration.showNotification(title, options);
    }
    if (msg.data?.type === 'loginStatus') {
        emitLoginStatus();
    }
}

/** Notification key */
const SENT_TO_DOOR_ACTION = 'SENT';

/** Show push notifications that come via service worker */
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


// Store access tokens in the service worker. The downside to this is that a service worker upgrade will lose the
// tokens!
let mostRecentTokens: null | dto.LoginDTO = null

/** Lets the applications know the current login status */
function emitLoginStatus(): Promise<void> {
    return messageClients(mostRecentTokens == null ? 'login:bad' : 'login:ok')
}


const authorizedPathPrefixes = ['/departures'] // prefixes we need to auth for
const noAuthRequired = ['/departures/push/key'] // exceptions to those prefixes

// Handle authentication transparently
self.addEventListener('fetch', (ev) => {
    const url = new URL(ev.request.url)
    // TODO: add hostname verification for extra security

    // In case of successful login, store tokens in service worker
    if (ev.request.url.endsWith("/login")) {
        const authReq = new Request(ev.request)
        ev.respondWith(fetch(authReq).then( resp => {
            if (resp.ok) {
                return resp.json().then((login: dto.LoginDTO) => {
                    mostRecentTokens = login
                    return emitLoginStatus().then ( () => {
                        return new Response(null, {})
                    })
                })
            } else {
                return resp;
            }
        }))
    } else if (
        authorizedPathPrefixes.filter(prefix => url.pathname.startsWith(prefix)).length &&
        !noAuthRequired.filter(path => url.pathname === path).length
    ) {
        if (mostRecentTokens != null) {
            const { accessToken, refreshToken } = mostRecentTokens;
            const reqWithAuth = new Request(ev.request, { headers: {
                Authorization: `Bearer ${accessToken}`
            } })
            ev.respondWith(fetch(reqWithAuth).then( resp => {
                if (resp.status == 401) {
                    // refresh tokens!
                    return fetch("/security/refresh-token", { method: 'POST', headers: {
                        Authorization: `Bearer ${refreshToken}`
                    }}).then((refreshResp) => {
                        if (refreshResp.ok) {
                            // great, token refreshed, try the original request again and let the chips fall where they may :P
                            return refreshResp.json().then((refreshedTokens: dto.LoginDTO) => {
                                mostRecentTokens = refreshedTokens;
                                const repeatOriginal = new Request(ev.request, {
                                    headers: {
                                        Authorization: `Bearer ${refreshedTokens.accessToken}`
                                    }
                                });
                                return fetch(repeatOriginal);
                            })
                        } if (refreshResp.status === 401) {
                            // something wrong, could not refresh token!
                            // delete tokens (..though a grace period could be given?) and return original response
                            mostRecentTokens = null;
                            return emitLoginStatus().then ( () => {
                                return resp;
                            })
                        } else {
                            // something else was wrong, huh.. well, just return original response?
                            return resp;
                        }
                    })
                } else {
                    // whatever was returned originally
                    return resp;
                }
            }))

        }
    }
});