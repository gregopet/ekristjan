import { createApp } from 'vue';
import router from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import {isSendPupilEvent} from "@/dto";
import { eventBus } from './events';
import '@/assets/base.css';

// receive communication from service worker here, passing it onto the message bus
navigator.serviceWorker.onmessage = (ev) => {
    if (isSendPupilEvent(ev.data)) {
        eventBus.emit("SendPupil", ev.data)
    } else {
        console.log("Received message from service worker", ev)
    }
}

const app = createApp(RootComponent)
app.use(router)
app.mount('#app')