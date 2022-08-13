<template>
  <h5 class="addClassesHeader sectionHeading" v-if="selectedClasses.length">Prisotni</h5>
  <ul class="classList">
    <li v-for="pupil in presentPupils.sort(sorting)" :class="{ departed: pupil.departure, summoned: pupil.summon }">
      <span>{{ pupil.pupil.name }}</span>
      <span>{{ formatSeconds(pupil.departurePlan.time) }}</span>
    </li>
  </ul>

  <h5 class="addClassesHeader sectionHeading" v-if="selectedClasses.length">Odšli / odsotni</h5>
  <ul class="classList">
    <li v-for="pupil in departedPupils.sort(sorting)" :class="{ departed: pupil.departure, summoned: pupil.summon }">
      <span>{{ pupil.pupil.name }}</span>
      <span>{{ formatDate(pupil.departure.time) }}</span>
    </li>
  </ul>

</template>

<script lang="ts" setup>
import {computed, ref, watch} from "vue";
import {pupils} from "@/data";
import {format, parse} from "date-fns";
import {date2Time, stripSeconds} from "@/formatters";
import {useInterval} from "@vueuse/core";

const props = defineProps<{
  selectedClasses: string[],
}>()

const formatSeconds = stripSeconds;
const formatDate = date2Time;

/** Defines the sort order of pupils */
const sorting = ref((pup1: dto.DailyDeparture, pup2: dto.DailyDeparture) => {
  return pup1.pupil.name.localeCompare(pup2.pupil.name)
})

const watchedPupils = computed(() => {
  return pupils.filter( pup => props.selectedClasses.indexOf(pup.pupil.fromClass) >= 0)
})

interface DepartedPupil extends dto.DailyDeparture {
  departure: dto.Departure;
}

const presentPupils = computed( ()=> {
  return watchedPupils.value.filter(pup => !pup.departure)
})

const departedPupils = computed( ()=> {
  return watchedPupils.value.filter(pup => pup.departure) as DepartedPupil[];
})

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

.classList {
  list-style-type: none;
  padding: 0.5em $padding-left 1em $padding-left;

  li {
    display: flex;
    justify-content: space-between;
    &.summoned { color: #ff3f0e; }
    &.departed { color: #7a828a; }
  }
}
</style>