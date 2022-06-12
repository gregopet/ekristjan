<template>
      <h4>Poišči učenca</h4>
      <input v-model="search" ref="searchElement" autofocus>
      <button @click="resetSearch">Prekliči</button>
      <ul class="selectPupil">
        <li v-for="[cls, pupils] in filtered">
          <h3>{{cls}}</h3>
          <ul>
            <li v-for="pupil in pupils">
              <a href="#" @click.prevent="sendPupil(pupil, cls)">{{ pupil.name }}</a>
            </li>
          </ul>
        </li>
      </ul>
</template>

<script setup lang="ts">
import {computed, type Ref, ref} from "vue";
import fakestate, { type Data, type Pupil} from "../data";
import {sendMessage} from '../main';
import type { SendPupil } from '../events';
import { refDebounced } from '@vueuse/core';

const search = ref('');
const searchDebounced = refDebounced(search, 200)
const searchElement = ref(null) as Ref<HTMLElement | null>
const filtered: Ref<[string, Pupil[]][]> = computed(() => {
  if (!searchDebounced.value) {
    return []
  }
  const searchWordsLowercase = searchDebounced.value.toLocaleLowerCase().split(" ").filter( w => !!w)
  const searchFn = (pupil: Pupil) => studentMatches(searchWordsLowercase, pupil)

  return Array.from(fakestate.classes)
    .map( ([clazz, pupList]) => [clazz, pupList.pupils.filter(searchFn)] as [string, Pupil[]])
    .filter( (entry) => entry[1].length > 0)
})

function resetSearch() {
  search.value = ''
  searchElement.value?.focus()
}

/** Sends notification that this pupil should come to the door */
function sendPupil(pupil: Pupil, fromClass: string) {
  const payload: SendPupil = { pupil, fromClass };
  sendMessage(JSON.stringify(payload))
  resetSearch()
}

/** Matches pupils by string */
function studentMatches(searchWordsLowercase: string[], pupil: Pupil) {
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