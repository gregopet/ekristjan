<template>
  <h5 class="classListHeader noClasses sectionHeading" v-if="summoned.length">Čakajo na potrditev:</h5>
  <ul>
    <li v-for="pupil in summoned">
      <span>{{ pupil.pupil.name }}</span>
      <span>{{ formatDate(pupil.summon.time) }}</span>
    </li>
  </ul>
</template>

<script lang="ts" setup>

import {pupils} from "@/data";
import {computed} from "vue";
import {date2Time} from "@/formatters";

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

<style lang="scss" scoped>
$padding-left: 20px;

.sectionHeading {
  font-size: 20px;
  width: 100%;
  padding: 0 0 0 $padding-left;
  &:first-child {
    padding-top: 0.5em;
  }
}

ul {
  list-style-type: none;
  padding: 0.5em $padding-left 1em $padding-left;

  li {
    display: flex;
    justify-content: space-between;
  }
}
</style>