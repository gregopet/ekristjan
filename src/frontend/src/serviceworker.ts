/// <reference lib="WebWorker" />

import { cleanupOutdatedCaches, precacheAndRoute } from 'workbox-precaching'
import { clientsClaim } from 'workbox-core';

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
 * React to messages from clients.
 * @param msg The message; it has a type and a payload.
 *  The currently supported types:
 *      - notification (send a notification), payload is {title, NotificationOptions}
 */
self.onmessage = (msg) => {
    if (msg.data?.type === 'notification') {
        // Notifications on Android need to be fired from the service worker!
        const { title, options } = msg.data;
        self.registration.showNotification(title, options);
    }
}