<template>

    <router-view></router-view>

</template>

<script lang="ts" setup>
import {useFetch, useWakeLock} from "@vueuse/core";
import {pupils} from "@/data";
import {type Ref, ref, watch} from "vue";
import { useSecureFetch } from '@/security';
import { forceLoginScreen } from "@/router";

// Load pupils from server - move this, it is blocking the login handler!!!
/*const { isFinished, error, data } = useSecureFetch("/departures/pupils/1A,2A")
watch(data as Ref<string>, (newData: string) => {
  try {
    const newPupils = JSON.parse(newData) as dto.Pupil[];
    if (newData) {
      pupils.push(...newPupils);
    }
  } catch (err) {
    // vueUse will still call this block, even in case of errors (like auth errors) :(
  }
})
watch(error, (err => { }))*/




// Keep screen turned on because we want those notifications!
const { isSupported, isActive, request, release } = useWakeLock()
request('screen')
</script>