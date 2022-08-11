<template>
  <p>
    <a href="#" @click.prevent="$router.go(-1)">Nazaj</a>
  </p>
  <h4>
    Izberi učenca iz {{ selectedClass }}
  </h4>
  <ul class="selectPupil">
    <li v-for="departure in sortPupils(shownPupils)">
      <a href="#" @click.prevent="sendPupil(departure.pupil)">{{ departure.pupil.name }}</a>
    </li>
  </ul>
</template>

<script lang="ts" setup>
import {computed, defineProps} from "vue";
import {useRouter} from "vue-router";
import {pupilsFromClass} from "@/data";

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
  await fetch("/pupils/leave", {
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