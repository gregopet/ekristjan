import Classroom from "@/components/Classroom.vue";
import Door from "@/components/Door.vue";
import Login from "@/components/Login.vue";
import ResetPassword from "@/components/ResetPassword.vue";
import RequestPasswordReset from "@/components/RequestPasswordReset.vue";
import OpeningSelector from "@/components/OpeningSelector.vue";
import { createRouter, createWebHashHistory } from 'vue-router';
import { isLoggedIn } from "@/security";

const publicRouteMeta = { pub: true }

const routes = [
    { path: '/', component: OpeningSelector, name: 'landing' },
    { path: '/prijava', component: Login, name: 'login', meta: publicRouteMeta },
    { path: '/zahtevaj-ponastavitev-gesla', component: RequestPasswordReset, name: 'requestPasswordReset', meta: publicRouteMeta },
    { path: '/ponastavi-geslo/:token', component: ResetPassword, name: 'resetPassword', meta: publicRouteMeta },
    { path: '/ucilnica', component: Classroom },
    { path: '/vrata', component: Door },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

router.beforeEach(async (to, from) => {
    // Users who are not logged in must first do so
    if (!to.meta.pub && !isLoggedIn()) {
        console.log("(PREVENTED) Navigation guard to", from)
        return { name: 'login' }
    }
    console.log("Allowing navigation from", from, "to", to)
})

export default router;

/** Force user to login screen, e.g. in case of errors */
export async function forceLoginScreen() {
    await router.push({ name: 'login' })
}