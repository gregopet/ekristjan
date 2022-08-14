<template>
  <Header :back="{ name: 'frontDoor'}"/>

  <h5 class="addClassesHeader sectionHeading">Izberi učenca iz {{ selectedClass }}</h5>
  <ul class="selectPupil">
    <li v-for="departure in sortPupils(shownPupils)">
      <a href="#" @click.prevent="sendPupil(departure.pupil)">{{ departure.pupil.name }}</a>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {useRouter} from "vue-router";
import {pupilsFromClass} from "@/data";
import Header from '../Header.vue';
import {useFetch} from "@vueuse/core";


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

<style lang="scss" scoped>
$padding-left: 20px;

.sectionHeading {
  font-size: 20px;
  width: 100%;
  margin-top: 1.0em;
  margin-bottom: 0.6em;
  padding: 0 0 0 $padding-left;
  &:first-child {
    padding-top: 0.5em;
  }
}
ul {
  list-style-type: none;
  padding-left: $padding-left;

  li {
     a {
       color: black;
       text-decoration: none;
     }
    height: 2.5em;
  }
}

</style>