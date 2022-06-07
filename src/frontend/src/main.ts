import { createApp } from 'vue';
import App from './App.vue';
import SockJS from 'sockjs-client';
import { type SendPupil, isSendPupil, eventBus } from './events';

const app = createApp(App).mount('#app')


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

