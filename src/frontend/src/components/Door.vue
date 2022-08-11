<template>
      <p>
        <router-link to="/">Nazaj</router-link>
      </p>
      <h4>Poišči učenca</h4>
      <input v-model="search" ref="searchElement" autofocus>
      <button @click="resetSearch">Prekliči</button>
      <ul class="selectPupil">
        <li v-for="departure in filtered">
          <a href="#" @click.prevent="sendPupil(departure.pupil)">{{ departure.pupil.name }}, {{ departure.pupil.fromClass }}</a>
        </li>
      </ul>
</template>

<script setup lang="ts">
import {computed, type Ref, ref} from "vue";
import { pupils } from "../data";
import { refDebounced } from '@vueuse/core';

const search = ref('');
const searchDebounced = refDebounced(search, 200)
const searchElement = ref(null) as Ref<HTMLElement | null>
const filtered: Ref<dto.DailyDeparture[]> = computed(() => {
  if (!searchDebounced.value) {
    return []
  }
  const searchWordsLowercase = searchDebounced.value.toLocaleLowerCase().split(" ").filter( w => !!w)
  const searchFn = (departure: dto.DailyDeparture) => studentMatches(searchWordsLowercase, departure.pupil)

  return pupils.filter(searchFn)
})

function resetSearch() {
  search.value = ''
  searchElement.value?.focus()
}

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
  resetSearch()
}

/** Matches pupils by string */
function studentMatches(searchWordsLowercase: string[], pupil: dto.Pupil) {
  for (let term of searchWordsLowercase) {
    if (pupil.name.toLowerCase().indexOf(term) < 0) { return false }
  }
  return true
}
</script>


<style lang="scss" scoped>

ul.selectPupil {
  list-style-type: none;
  li {
    margin: 0.25em 0.25em;
    ul {
      list-style-type: none;
    }
  }
}

@keyframes blink{
  0% {
    background: white;
  }
  50% {
    background: red;
  }
  100% {
    background: white;
  }
}
</style>
