import { createApp } from 'vue';
import router from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import { registerSW } from 'virtual:pwa-register';
import {isSendPupilEvent} from "@/dto";
import { eventBus } from './events';

// receive communication from service worker here, passing it onto the message bus
navigator.serviceWorker.onmessage = (ev) => {
    if (isSendPupilEvent(ev.data)) {
        eventBus.emit("SendPupil", ev.data)
    } else {
        console.log("Received message from service worker", ev)
    }
}

// disable service worker when we want a fast reload!
/*registerSW({
    immediate: true,
})*/

const app = createApp(RootComponent)
app.use(router)
app.mount('#app')