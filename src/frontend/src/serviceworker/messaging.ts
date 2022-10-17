import {EVENT_LOGIN_FAILED} from "@/serviceworker/authorization";
import type {AccessToken} from "@/AccessToken";

declare let self: ServiceWorkerGlobalScope

/**
 * Messages all clients of a service worker.
 * @param message The message to send
 */
export async function messageClients(message: any) {
    const clients = await self.clients.matchAll({ includeUncontrolled: true, type: "window"})
    for (const client of clients) {
        client?.postMessage(message);
    }
}

/** Returns true if a client message is a "login failed" message */
export function isMessageLoginFailed(data: any): boolean {
    return data === EVENT_LOGIN_FAILED;
}

/** Returns true if the message is an access token */
export function isMessageLoginSuccess(data: any): data is AccessToken {
    return data && data.iat;
}