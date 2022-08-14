import Classroom from "@/components/classroom/Classroom.vue";
import DoorPickClass from "@/components/door/DoorPickClass.vue";
import DoorPickPupil from "@/components/door/DoorPickPupil.vue";
import Login from "@/components/Login.vue";
import ResetPassword from "@/components/passwordReset/ResetPassword.vue";
import RequestPasswordReset from "@/components/passwordReset/RequestPasswordReset.vue";
import LoggedInComponent from "@/components/LoggedInComponent.vue";
import { createRouter, createWebHashHistory } from 'vue-router';
import { loggedIn } from '@/main';

const publicRouteMeta = { pub: true }

const routes = [
    { path: '/', redirect: '/prijavljen' },
    { path: '/prijava', component: Login, name: 'login', meta: publicRouteMeta },
    { path: '/zahtevaj-ponastavitev-gesla', component: RequestPasswordReset, name: 'requestPasswordReset', meta: publicRouteMeta },
    { path: '/ponastavi-geslo/:token', component: ResetPassword, name: 'resetPassword', meta: publicRouteMeta },
    { path: '/prijavljen', component: LoggedInComponent, name: 'landing', children: [
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
    if (!to.meta.pub && !loggedIn.value) {
        console.log("(PREVENTED) Navigation guard to", from)
        return { name: 'login' }
    }
})

export default router;

/** Force user to login screen, e.g. in case of errors */
export async function forceLoginScreen() {
    await router.push({ name: 'login' })
}