/** Gets the push notification key from the server */
import {secureFetch} from "@/security";

export async function getServerKey(): Promise<string> {
    return (await fetch("/departures/push/key")).text()
}

/** Sets the browser up to receive push notifications */
export async function subscribeOnClient(key: string): Promise<PushSubscription> {
    // current subscription?
    const sw = await navigator.serviceWorker.getRegistration()
    // TODO display an error if server worker couldn't register?
    const current = await sw!.pushManager.getSubscription()
    if (current && arrayBufferToBase64(current.options.applicationServerKey) !== key) {
        await current.unsubscribe();
    }

    const registration = await navigator.serviceWorker.getRegistration()
    return await registration!.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: key,
    })
}

/** Sets the server up to send push notifications */
export async function subscribeOnServer(sub: PushSubscription, fromClasses: string[]): Promise<Response> {
    const { keys, endpoint } = sub.toJSON();
    const payload: dto.PushSubscription = {
        keys: (keys as any) as dto.Keys,
        endpoint: endpoint!,
        fromClasses,
    }
    return secureFetch("/departures/push/subscribe", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
}

// https://stackoverflow.com/questions/9267899/arraybuffer-to-base64-encoded-string
function arrayBufferToBase64( buffer: ArrayBuffer | null ): string | null {
    if (buffer == null) return null;
    let binary = '';
    let bytes = new Uint8Array( buffer );
    let len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode( bytes[ i ] );
    }
    return window.btoa( binary );
}