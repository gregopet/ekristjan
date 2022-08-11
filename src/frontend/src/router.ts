import Classroom from "@/components/Classroom.vue";
import DoorPickClass from "@/components/DoorPickClass.vue";
import DoorPickPupil from "@/components/DoorPickPupil.vue";
import Login from "@/components/Login.vue";
import ResetPassword from "@/components/ResetPassword.vue";
import RequestPasswordReset from "@/components/RequestPasswordReset.vue";
import OpeningSelector from "@/components/OpeningSelector.vue";
import { createRouter, createWebHashHistory } from 'vue-router';
import { isLoggedIn } from "@/security";

const publicRouteMeta = { pub: true }

const routes = [
    { path: '/', redirect: '/prijavljen' },
    { path: '/prijava', component: Login, name: 'login', meta: publicRouteMeta },
    { path: '/zahtevaj-ponastavitev-gesla', component: RequestPasswordReset, name: 'requestPasswordReset', meta: publicRouteMeta },
    { path: '/ponastavi-geslo/:token', component: ResetPassword, name: 'resetPassword', meta: publicRouteMeta },
    { path: '/prijavljen', component: OpeningSelector, name: 'landing', children: [
        { path: 'ucilnica', component: Classroom, name: 'classroom' },
        { path: 'vrata', component: DoorPickClass, name: 'frontDoor' },
        { path: 'vrata/:selectedClass', component: DoorPickPupil, name: 'frontDoorWithClazz', props: true },
    ]},
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
})

export default router;

/** Force user to login screen, e.g. in case of errors */
export async function forceLoginScreen() {
    await router.push({ name: 'login' })
}