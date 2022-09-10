<template>
  <h5 class="text-xl pt-5 pl-5" v-if="selectedClasses.length">Prisotni</h5>
  <ul class="px-5 pt-1">
    <li v-for="pupil in presentPupils.sort(sorting)" @click="pupilWithDialog = pupil" :class="{ 'text-red-500': pupil.summon }" class="flex justify-between">
      <span>{{ pupil.pupil.name }}</span>
      <span class="flex items-center">
        <img src="../../assets/otrok.svg" title="Odide sam" v-if="pupil.leavesAlone" class="h-4 inline mr-1 opacity-75">
        {{ formatSeconds(pupil.departurePlan.time) }}
      </span>
    </li>
  </ul>

  <h5 class="text-xl pt-2 pl-5" v-if="selectedClasses.length">Odšli / odsotni</h5>
  <ul class="px-5 pt-1">
    <li v-for="pupil in departedPupils.sort(sorting)" @click="pupilWithDialog = pupil" :class="{ 'text-gray-500': pupil.departure }" class="flex justify-between">
      <span>{{ pupil.pupil.name }}</span>
      <span>
        {{ formatDate(pupil.departure.time) }}
      </span>
    </li>
  </ul>

  <PupilDialog v-if="pupilWithDialog" :pupil="pupilWithDialog" @close="pupilWithDialog = null" />
</template>

<script lang="ts" setup>
import {computed, ref, watch} from "vue";
import {pupils} from "@/data";
import {useInterval} from "@vueuse/core";
import PupilDialog from "@/components/classroom/PupilDialog.vue";
import {date2Time, stripSeconds} from "@/dateAndTime";

const props = defineProps<{
  selectedClasses: string[],
}>()

const formatSeconds = stripSeconds;
const formatDate = date2Time;

const pupilWithDialog = ref<dto.DailyDeparture | null>(null)

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