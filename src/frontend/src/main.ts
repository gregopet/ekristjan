import {createApp, ref} from 'vue';
import router from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import {isSendPupilEvent} from "@/dto";
import { eventBus } from './events';
import '@/assets/base.css';

/** Is the user currently logged in? */
export const loggedIn = ref(false)

let loginStatusFirstReceived = false

// receive communication from service worker here, passing it onto the message bus
navigator.serviceWorker.onmessage = (ev) => {
    if (ev.data === "login:ok") {
        loggedIn.value = true
        if (!loginStatusFirstReceived) startApp()
        loginStatusFirstReceived = true;
    }
    else if (ev.data === "login:bad") {
        loggedIn.value = false
        if (!loginStatusFirstReceived) startApp()
        loginStatusFirstReceived = true;
    }
    else if (isSendPupilEvent(ev.data)) {
        eventBus.emit("SendPupil", ev.data)
    } else {
        console.log("Received message from service worker", ev)
    }
}

navigator.serviceWorker.controller!.postMessage({ type: 'loginStatus' })

// start app after polling service worker
function startApp() {
    const app = createApp(RootComponent)
    app.use(router)
    app.mount('#app')
}