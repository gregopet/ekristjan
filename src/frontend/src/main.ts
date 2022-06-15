import { createApp } from 'vue';
import router from '@/router';
import RootComponent from '@/components/RootComponent.vue';
import { registerSW } from 'virtual:pwa-register';

// receive communication from service worker here, passing it onto the message bus
navigator.serviceWorker.onmessage = (ev) => {
    console.log("Received message from service worker", ev)
}

registerSW({
    immediate: true,
})

const app = createApp(RootComponent)
app.use(router)
app.mount('#app')