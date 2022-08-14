<template>
  <main>
    <div v-if="error" class="error">
      Pozor: napaka pri komunikaciji, preverite povezavo!
    </div>
    <div v-if="showSelector">
    <Header />
      <h1>
        Kje sem?
      </h1>
      <div>
        <h2>
          <router-link :to="{ name: 'frontDoor' }">Na vratih</router-link>
        </h2>
      </div>
      <div>
        <h2>
          <router-link :to="{ name: 'classroom' }">V učilnici</router-link>
        </h2>
      </div>
    </div>
    <router-view></router-view>
  </main>
</template>

<script lang="ts" setup>
import {computed, defineComponent, type Ref, ref, watch} from "vue";
import {useRoute} from "vue-router";
import {pupils} from "@/data";
import Header from "@/components/Header.vue";
import {useInterval, useIntervalFn} from "@vueuse/core";

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



// Load pupils from server, replacing any existing students already there
/*const { isFinished, error, data } = useSecureFetch("/departures/pupils/")
watch(data as Ref<string>, (newData: string) => {
  try {
    const newPupils = JSON.parse(newData) as dto.DailyDeparture[];
    if (newData) {
      pupils.splice(0, pupils.length,...newPupils)
    }
  } catch (err) {
    // vueUse will still call this block, even in case of errors (like auth errors) :(
  }
})
watch(error, (err => { }))*/


</script>

<style lang="scss">
  .error {
    background-color: red;
    color: white;
    font-size: 24px;
    text-shadow: 1px 1px #9b0101;
    padding-left: 15px;
  }
</style>
