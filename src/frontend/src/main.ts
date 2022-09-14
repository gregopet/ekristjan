import {createApp, ref} from 'vue';
import router, {forceLoggedInLanding, forceLoginScreen, isOnProtectedRoute} from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import {isSendPupilEvent} from "@/dto";
import { eventBus } from './events';
import '@/tailwind.css';
import {Workbox} from "workbox-window";
import {EVENT_LOGIN_FAILED, EVENT_LOGIN_SUCCESS} from "@/serviceworker/authorization";

/** Is the user currently logged in? */
export const loggedIn = ref(false)

/** Install the service worker */
const wb = new Workbox('/serviceworker.js');
wb.register().then(determineLoginStatus);

// Recover after hard refreshes (they prevent the service worker from registering)
// https://stackoverflow.com/questions/51597231/register-service-worker-after-hard-refresh
navigator.serviceWorker.getRegistration().then( reg => {
    // There's an active SW, but no controller for this tab.
    if (reg && reg.active && !navigator.serviceWorker.controller) {
        // Perform a soft reload to load everything from the SW and get
        // a consistent set of resources.
        window.location.reload();
    }
});

// receive communication from service worker here, passing it onto the message bus
navigator.serviceWorker.onmessage = (ev) => {
    if (isSendPupilEvent(ev.data)) {
        eventBus.emit("SendPupil", ev.data)
    } else if (ev.data === EVENT_LOGIN_FAILED) {
        loggedIn.value = false;
        if (isOnProtectedRoute()) {
            forceLoginScreen();
        }
    } else if (ev.data === EVENT_LOGIN_SUCCESS) {
        loggedIn.value = true;
        if (!isOnProtectedRoute()) {
            forceLoggedInLanding(); // is this OK? Is there a valid thing they could be doing somewhere else while logged in?
        }
    } else {
        console.log("Received message from service worker", ev)
    }
}


function determineLoginStatus() {
    console.debug("Asking the service worker what our login status is")
    wb.messageSW({ type: 'loginStatus' }).then(
        (loginStatus: boolean) => {
            console.debug("Detected initial login state as", loginStatus);
            loggedIn.value = loginStatus;
            startApp(loginStatus);
        },
        (error) => {
            console.error("Error determining whether user is already logged in, aborting app startup", error)
        }
    );
}

/** Logs out of the application */
export function logout() {
    console.debug("Requestiong logout")
    wb.messageSW({ type: 'logout' })
}

function startApp(loggedIn: boolean) {
    const app = createApp(RootComponent)
    app.use(router)
    const rootContainer = document.getElementById("app");
    if (!rootContainer) console.error("Could not find root element to mount app on!")
    else app.mount(rootContainer);

    if (loggedIn && router.currentRoute.value.params.pub) {
        router.replace({ name: 'landing' })
    }
}