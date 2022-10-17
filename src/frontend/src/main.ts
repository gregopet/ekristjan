import {createApp, ref} from 'vue';
import router, {forceLoggedInLanding, forceLoginScreen, isOnProtectedRoute} from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import {isSendPupilEvent} from "@/dto";
import { eventBus } from './events';
import '@/tailwind.css';
import {Workbox} from "workbox-window";
import {EVENT_LOGIN_FAILED, EVENT_LOGIN_SUCCESS} from "@/serviceworker/authorization";
import {sendLog} from "@/diagnostics";
import Toast from "vue-toastification";
import type {ToastOptions} from "vue-toastification/dist/types/types";
import "vue-toastification/dist/index.css";
import type {AccessToken} from "@/AccessToken";
import {isMessageLoginFailed, isMessageLoginSuccess} from "@/serviceworker/messaging";

/** Is the user currently logged in? */
export const loggedIn = ref<null | AccessToken>(null)

/*
 * Install the service worker manually
 * https://vite-plugin-pwa.netlify.app/guide/development.html
 */
const sWorkerLocation = import.meta.env.MODE === "production" ? '/serviceworker.js' : '/dev-sw.js?dev-sw';
const sWorkerOptions = import.meta.env.MODE === "production" ? {} : { type: 'module' };
console.log("Service worker ", sWorkerLocation, "with options", sWorkerOptions);
const wb = new Workbox(sWorkerLocation, sWorkerOptions);
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
    } else if (isMessageLoginFailed(ev.data)) {
        loggedIn.value = null;
        if (isOnProtectedRoute()) {
            sendLog("login", "debug", "Login failed, sending to login page!")
            forceLoginScreen();
        }
    } else if (isMessageLoginSuccess(ev.data)) {
        loggedIn.value = ev.data;
        if (!isOnProtectedRoute()) {
            sendLog("login", "debug", "Somebody logged us in, sending user to logged in page")
            forceLoggedInLanding(); // is this OK? Is there a valid thing they could be doing somewhere else while logged in?
        }
    } else {
        console.log("Received message from service worker", ev)
    }
}


function determineLoginStatus() {
    console.debug("Asking the service worker what our login status is")
    wb.messageSW({ type: 'loginStatus' }).then(
        (loginStatus: null | AccessToken) => {
            console.debug("Detected initial login state as", loginStatus);
            loggedIn.value = loginStatus;
            startApp(loginStatus !== null);
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
    registerToastPlugin(app);
    const rootContainer = document.getElementById("app");
    if (!rootContainer) console.error("Could not find root element to mount app on!")
    else app.mount(rootContainer);

    if (loggedIn && router.currentRoute.value.params.pub) {
        router.replace({ name: 'landing' })
    }
}

function registerToastPlugin(app: any) {
    const options: ToastOptions = {
        toastClassName: "toasty"
    };
    app.use(Toast, options);
}