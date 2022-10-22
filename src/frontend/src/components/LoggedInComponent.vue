<template>
  <LoggedInLayout>
    <main>
      <div v-if="error" class="bg-red-500 text-red-50 p-4 text-xl fixed" style="text-shadow: 1px 1px #9b0101; z-index: 500">
        Pozor: napaka pri komunikaciji, preverite povezavo!
      </div>
      <div v-if="showSelector" class="min-h-screen flex flex-col justify-center pt-8 pb-16">
        <h1 class="font-logo text-5xl text-center text-blue mb-5">eKristjan</h1>

        <div class="text-xl pt-10 font-sans uppercase text-blue">
          <router-link :to="{ name: 'classroom' }" class="rounded-r-[20px] w-[70%] m-auto cursor-pointer drop-shadow-lg flex justify-between items-stretch bg-gradient-to-r from-gray-100 to-white">
            <div class="block py-3 basis-[80%] text-right">Učilnica</div>
            <div class="w-3 basis-[30px] rounded-r-[20px] bg-gradient-to-r from-my-green to-my-green-light"> </div>
          </router-link>

          <router-link :to="{ name: 'frontDoor' }" class="rounded-r-[20px] w-[70%] m-auto cursor-pointer drop-shadow-lg flex justify-between items-stretch bg-gradient-to-r from-gray-100 to-white mt-8">
            <div class="block py-3 basis-[80%] text-right">Vrata</div>
            <div class="w-3 basis-[30px] rounded-r-[20px] bg-gradient-to-r from-sandy to-sandy-light"> </div>
          </router-link>

          <router-link :to="{ name: 'officePupils' }" v-if="showOffice" class="rounded-r-[20px] w-[70%] m-auto cursor-pointer drop-shadow-lg flex justify-between items-stretch bg-gradient-to-r from-gray-100 to-white mt-8">
            <div class="block py-3 basis-[80%] text-right">Pisarna</div>
            <div class="w-3 basis-[30px] rounded-r-[20px] bg-gradient-to-r from-my-blue to-my-blue-light"> </div>
          </router-link>


          <a @click.prevent="logout" class="text-left rounded-l-[20px] w-[70%] m-auto cursor-pointer drop-shadow-lg bg-gray-100 flex justify-between items-stretch mt-16">
            <div class="w-3 basis-[30px] rounded-l-[20px] bg-gradient-to-l from-red-600 to-red-200"> </div>
            <a href="#" class="block py-3 basis-[80%] text-left">Odjava</a>
          </a>
        </div>
      </div>
      <div v-else class="min-h-screen">
        <router-view></router-view>
      </div>
    </main>
  </LoggedInLayout>
</template>

<script lang="ts" setup>
import {computed, defineComponent, type Ref, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {pupils} from "@/data";
import Header from "@/components/Header.vue";
import {useInterval, useIntervalFn} from "@vueuse/core";
import {loggedIn, logout as doLogout} from "@/main";
import LoggedInLayout from './LoggedInLayout.vue';
import {hasBackofficePermission} from "@/AccessToken";

const route = useRoute()
const showSelector = computed(() => route.name === 'landing');

/** Show the office navigation link? */
const showOffice = computed(() => hasBackofficePermission(loggedIn.value))

// fetch pupils repeatedly
const error = ref(false)
async function fetchPupils() {
  const resp = await fetch("/departures/pupils/")
  if (resp.ok) {
    error.value = false
    const newPupils = await resp.json() as dto.DailyDeparture[];
    mergePupils(newPupils);
  }
  error.value = !resp.ok
}

/** Overwrites properties of existing pupils so their properties update even when injected into a dialogue or somewhere */
function mergePupils(newPupils: dto.DailyDeparture[]) {
  // remove non-existant & update existing
  let pos = 0
  while (pos < pupils.length) {
    const newVersion = newPupils.find(pup => pup.pupil.id === pupils[pos].pupil.id);
    if (newVersion) {
      // Iterating through all properties could theoretically work, but we'd miss if any property became undefined
      // in the new state
      pupils[pos].pupil = newVersion.pupil
      pupils[pos].departure = newVersion.departure
      pupils[pos].leavesAlone = newVersion.leavesAlone
      pupils[pos].day = newVersion.day
      pupils[pos].departurePlan = newVersion.departurePlan
      pupils[pos].summon = newVersion.summon
      pos++
    } else {
      pupils.splice(pos, 1)
    }
  }

  // insert new ones
  newPupils.forEach( (newPup) => {
    if (!pupils.some(oldPup => oldPup.pupil.id === newPup.pupil.id)) {
      pupils.push(newPup);
    }
  })
}


useIntervalFn(fetchPupils, 2000, { immediate: true, immediateCallback: true })

// logout users
function logout() {
  doLogout();
}


</script>
