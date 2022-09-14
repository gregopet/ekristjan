<template>

  <div class="bg-red-50 pb-5" v-if="scheduledOrSummonedPupils.length">
    <h5 class="text-2xl pt-3 px-5 flex justify-between items-center">
      <span>V odhodu</span>
      <span :style="{ 'background-color': statusColor }" class="h-5 w-5 rounded border-2 border-gray-400 transition-colors duration-[1s]"></span>
    </h5>
    <ul class="px-5 pt-2">
      <li v-for="pupil in scheduledOrSummonedPupils.sort(departureSorting)" :key="leavingPupilUIKey(pupil)" @click="pupilWithDialog = pupil" class="
        flex justify-between text-xl
        animate-[bounce_1s_ease-in-out_3]
      ">
        <span>{{ pupil.pupil.name }}</span>
        <span class="flex items-center">
          <img src="../../assets/otrok.svg" title="Odide sam" v-if="leavesAlone(pupil)" class="h-4 inline mr-1 opacity-75">
          {{ scheduledOrSummonedTime(pupil) }}
        </span>
      </li>
    </ul>
  </div>

  <h5 class="text-xl pt-5 pl-5" v-if="selectedClasses.length">
    Prisotni
  </h5>
  <ul class="px-5 pt-1">
    <li v-for="pupil in presentPupils.sort(sorting)" @click="pupilWithDialog = pupil" :class="{ 'text-red-500': pupil.summon }" class="flex justify-between">
      <span>{{ pupil.pupil.name }}</span>
      <span class="flex items-center">
        <img v-if="leavesAlone(pupil)" src="../../assets/otrok.svg" title="Odide sam" class="h-4 inline mr-1 opacity-75">
        {{ formatSeconds(pupil.departurePlan.time) }}
      </span>
    </li>
  </ul>

  <h5 class="text-xl pt-5 pl-5" v-if="departedPupils.length">
    Odšli / odsotni
  </h5>
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
import { pupils} from "@/data";
import {pupilDeparted, pupilIsSummoned, pupilLeavesAlone, pupilNeedsToDepart} from "@/pupil";
import {date2Time, stripSeconds} from "@/dateAndTime";
import PupilDialog from "@/components/classroom/PupilDialog.vue";
import SparkMD5 from "spark-md5";
const formatSeconds = stripSeconds;
const formatDate = date2Time;
const leavesAlone = pupilLeavesAlone

/** Type safety cast of pupils that have a departure */
interface DepartedPupil extends dto.DailyDeparture {
  departure: dto.Departure;
}

const props = defineProps<{
  selectedClasses: string[],
}>()

/** Determines and formats the time displayed in the 'need to leave!' section */
function scheduledOrSummonedTime(pupil: dto.DailyDeparture): string {
  return formatDate(pupil.summon?.time || null) ?? formatSeconds(pupil.departurePlan.time)
}

const pupilWithDialog = ref<dto.DailyDeparture | null>(null)

/** Defines the sort order of pupils */
const sorting = ref((pup1: dto.DailyDeparture, pup2: dto.DailyDeparture) => {
  return pup1.pupil.name.localeCompare(pup2.pupil.name)
})

/** Defines the sort order of pupils in the 'need to leave!' section. Sorts in reverse, so most recent entry is at top */
const departureSorting = ref((pup1: dto.DailyDeparture, pup2: dto.DailyDeparture) => {
  return scheduledOrSummonedTime(pup2).localeCompare(scheduledOrSummonedTime(pup1))
})

/** All the pupils we are even interested in */
const watchedPupils = computed(() => {
  return pupils.filter( pup => props.selectedClasses.indexOf(pup.pupil.fromClass) >= 0)
})

/** All pupils still present and not summoned */
const presentPupils = computed( ()=> {
  return watchedPupils.value.filter(pup => !pupilDeparted(pup) && !pupilNeedsToDepart(pup) && !pupilIsSummoned(pup))
})

/** All pupils who have departed */
const departedPupils = computed( ()=> {
  return watchedPupils.value.filter(pupilDeparted) as DepartedPupil[];
})

/** Pupils that were summoned or scheduled to depart */
const scheduledOrSummonedPupils = computed(() => watchedPupils.value.filter( (pup) => pupilNeedsToDepart(pup) || pupilIsSummoned(pup)));

/**
 * A function that calculates Vue's tracking key for pupils that need to depart. When the most recent summon changes the
 * element's key will change as well, recreating the element and restarting the animation.
 */
function leavingPupilUIKey(pup: dto.DailyDeparture): string {
  if (pup?.summon) return `summon_${pup.summon.id}`;
  else return '' + pup.pupil.id;
}

/**
 * Generates a random color to use for the current status of pupils waiting depart; the idea is to provide a visual
 * cue that the state has changed (in addition to the 'wobbly' animation of newly added waiting students).
 */
const statusColor = computed(() => {
  const base = scheduledOrSummonedPupils.value.map(leavingPupilUIKey).join()
  const hash = SparkMD5.hash(base);
  return "#" + hash.substring(0, 6);
});
</script>