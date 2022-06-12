// Methods to interact with the service worker

/** Creates a notification, has to be made in ServiceWorker on Android */
export function notification(title: string, options: NotificationOptions) {
    navigator.serviceWorker.controller!.postMessage({type: 'notification', title, options })
}