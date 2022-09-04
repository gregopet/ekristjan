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