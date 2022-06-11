import Classroom from "@/components/Classroom.vue";
import Door from "@/components/Door.vue";
import OpeningSelector from "@/components/OpeningSelector.vue";
import { createRouter, createWebHashHistory } from 'vue-router';

const routes = [
    { path: '/', component: OpeningSelector },
    { path: '/ucilnica', component: Classroom },
    { path: '/vrata', component: Door },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

export default router;