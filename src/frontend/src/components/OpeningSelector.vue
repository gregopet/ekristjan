<template>
  <main>
    <div v-if="showSelector">
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
import { useSecureFetch } from '@/security';

const route = useRoute()
const showSelector = computed(() => route.name === 'landing');


// Load pupils from server, replacing any existing students already there
const { isFinished, error, data } = useSecureFetch("/departures/pupils/")
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
watch(error, (err => { }))


</script>

<style lang="scss">

</style>
