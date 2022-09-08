<template>
  <LoggedInComponent>
    <main>
      <div v-if="error" class="bg-red-500 text-red-50 p-4 text-xl" style="text-shadow: 1px 1px #9b0101">
        Pozor: napaka pri komunikaciji, preverite povezavo!
      </div>
      <div v-if="showSelector">
        <Header />

        <div class="text-center space-y-4 text-2xl pt-10">
          <h1 class="mb-10">Kje sem?</h1>

          <h2 class="rounded bg-blue-600 w-[66%] text-blue-50 m-auto cursor-pointer">
            <router-link :to="{ name: 'frontDoor' }" class="block">Na vratih</router-link>
          </h2>

          <div>
            <h2 class="rounded bg-blue-600 w-[66%] text-blue-50 m-auto  cursor-pointer">
              <router-link :to="{ name: 'classroom' }" class="block">V učilnici</router-link>
            </h2>
          </div>
          <div>
            <h2 class="rounded bg-red-600 w-[66%] text-red-50 m-auto mt-12 cursor-pointer">
              <a href="#" @click.prevent="logout" class="block">Odjava</a>
            </h2>
          </div>
        </div>
      </div>
      <router-view></router-view>
    </main>
  </LoggedInComponent>
</template>

<script lang="ts" setup>
import {computed, defineComponent, type Ref, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {pupils} from "@/data";
import Header from "@/components/Header.vue";
import {useInterval, useIntervalFn} from "@vueuse/core";
import { logout as doLogout } from "@/main";
import LoggedInComponent from './LoggedInLayout.vue';

const route = useRoute()
const showSelector = computed(() => route.name === 'landing');


// fetch pupils repeatedly
const error = ref(false)
async function fetchPupils() {
  const resp = await fetch("/departures/pupils/")
  if (resp.ok) {
    error.value = false
    const newPupils = await resp.json() as dto.DailyDeparture[];
    pupils.splice(0, pupils.length,...newPupils)
  }
  error.value = !resp.ok
}


useIntervalFn(fetchPupils, 2000, { immediate: true, immediateCallback: true })

// logout users
function logout() {
  doLogout();
}


</script>
