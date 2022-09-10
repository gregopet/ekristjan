<template>
  <h5 class="text-xl pt-5 pl-5" v-if="summoned.length">Čakajo na potrditev:</h5>
  <ul class="p-5 mb-10">
    <li v-for="pupil in summoned" class="flex justify-between">
      <span>{{ pupil.pupil.name }}</span>
      <span>{{ formatDate(pupil.summon.time) }}</span>
    </li>
  </ul>
</template>

<script lang="ts" setup>

import {pupils} from "@/data";
import {computed} from "vue";
import {date2Time} from "@/dateAndTime";

const formatDate = date2Time;

interface SummonedPupil extends dto.DailyDeparture {
  summon: dto.Summon;
}

const summoned = computed(() =>
    pupils
    .filter(pupil => pupil.summon != null && !pupil.departure )
    .map(pupil => pupil as SummonedPupil)
)
</script>