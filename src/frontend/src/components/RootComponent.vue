<template>
  <div v-if="!isFinished">Prosim počakajte, nalagam spisek učencev</div>
  <div v-else-if="error">Pri nalaganju učencev je prišlo do napake {{ error }}</div>
  <div v-else>
    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { useFetch } from "@vueuse/core";
import {pupils} from "@/data";
import {type Ref, watch} from "vue";

const { isFinished, error, data } = useFetch("/pupils")
watch(data as Ref<string>, (newData: string) => {
  const newPupils = JSON.parse(newData) as dto.Pupil[];
  if (newData) {
    pupils.push(...newPupils);
  }
})
</script>