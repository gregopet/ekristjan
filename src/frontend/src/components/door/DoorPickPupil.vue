<template>
  <LoggedInLayout>
    <Header :back="{ name: 'frontDoor'}"/>

    <h5 class="text-xl p-5">Izberi učenca iz {{ selectedClass }}</h5>
    <ul class="p-5 space-y-5">
      <li v-for="departure in sortPupils(shownPupils)" :class="{'text-gray-600': departure.departure}" class="flex justify-between">
        <a href="#" @click.prevent="sendPupil(departure.pupil)">{{ departure.pupil.name }}</a>
        <span v-if="departure.departure">
          <span v-if="departure.departure.entireDay">
            (odsotnost cel dan)
          </span>
          <span v-else>
            (šel/šla ob {{ formatTime(departure.departure.time) }})
          </span>
        </span>
      </li>
    </ul>
  </LoggedInLayout>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {useRouter} from "vue-router";
import {pupilsFromClass} from "@/data";
import Header from '../Header.vue';
import {useFetch} from "@vueuse/core";
import LoggedInLayout from '../LoggedInLayout.vue';
import {date2Time} from "@/dateAndTime";
import {requestPupilSummon} from "@/pupil";


const formatTime = date2Time;

const props = defineProps({
  selectedClass: {
    type: String,
    required: true
  }
})

const router = useRouter()

/** Sorts pupils by name */
function sortPupils(pupils: dto.DailyDeparture[]): dto.DailyDeparture[] {
  return pupils.sort((pup1, pup2) => pup1.pupil.name.localeCompare(pup2.pupil.name));
}

/** Pupils to show in the pupil selector - empty array if no class is selected */
const shownPupils = computed(() => {
  if (props.selectedClass === '') {
    return []
  } else {
    return pupilsFromClass(props.selectedClass);
  }
})


/** Sends notification that this pupil should come to the door */
async function sendPupil(pupil: dto.Pupil) {
  const req = await fetch(requestPupilSummon(pupil))
  if (req.ok) {
    router.go(-1);
  } else {
    // TODO: let the caller know there was an error
  }
}

</script>