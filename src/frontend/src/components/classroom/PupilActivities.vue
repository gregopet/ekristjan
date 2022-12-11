<script lang="ts" setup>
import {useFetch} from "@vueuse/core";

const props = defineProps< {
  pupilId: number;
}>()

const { isFinished, data } = useFetch("/activities/pupil/" + props.pupilId).get().json()

</script>


<template>
  <div v-if="isFinished">
    <h2 v-if="data.length == 0" class="text-xl">Učenec ni vpisan na nobeno interesno dejavnost</h2>
    <div v-else>
      <h2 class="text-xl mb-1">Interesne dejavnosti:</h2>
      <ul>
        <li v-for="activity in data.sort()">{{activity}}</li>
      </ul>

    </div>
  </div>
</template>