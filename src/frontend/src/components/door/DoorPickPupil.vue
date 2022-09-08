<template>
  <LoggedInLayout>
    <Header :back="{ name: 'frontDoor'}"/>

    <h5 class="text-xl p-5">Izberi učenca iz {{ selectedClass }}</h5>
    <ul class="p-5 space-y-5">
      <li v-for="departure in sortPupils(shownPupils)">
        <a href="#" @click.prevent="sendPupil(departure.pupil)">{{ departure.pupil.name }}</a>
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
  useFetch("/departures/pupils/leave", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(pupil),
    credentials: "include",
  })
  router.go(-1);
}

</script>