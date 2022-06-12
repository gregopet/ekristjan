import { createApp } from 'vue';
import SockJS from 'sockjs-client';
import { type SendPupil, isSendPupil, eventBus } from './events';
import router from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import { registerSW } from 'virtual:pwa-register';

// receive communication from service worker here
navigator.serviceWorker.onmessage = (event) => {
    console.log("Got message from service worker", event.data)
}

registerSW({
    immediate: true,
})

const app = createApp(RootComponent)
app.use(router)
app.mount('#app')

let sock: WebSocket;
connect();

function connect() {

    sock = new SockJS('./sock');
    sock.onopen = (ev) => {
        console.log("Socket open")
    }
    sock.onmessage = (ev) => {
        const payload = JSON.parse(ev.data);
        if (isSendPupil(payload)) {
            eventBus.emit("SendPupil", payload);
        }
    }
    sock.onclose = (ev) => {
        connect(); // Handle reconnect!
    }
}

export function sendMessage(payload: string) {
    sock.send(payload);
}

