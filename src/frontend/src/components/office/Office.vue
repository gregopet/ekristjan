<template>
  <Header></Header>
  <div v-if="error">
    Napaka pri nalaganju podatkov!
  </div>
  <div v-else-if="isFetching">
    Nalagam podatke
  </div>
  <div v-else class="pt-3">

    <button @click="newPupil" class="m-2 bg-green-600 text-white rounded p-2">Nov učenec/učenka</button>

    <table class="table mt-1">
      <thead>
        <tr>
          <th class="text-left pl-3">Ime</th>
          <th class="min-w-[20px]"></th>
          <th colspan="6" class="text-center">Redni odhodi</th>
        </tr>
        <tr class="text-center">
          <th class="pl-3">
            <input type="search" class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" placeholder="Išči..." v-model="nameFilter" autofocus>
          </th>
          <th></th>
          <th class="min-w-[60px]">Sam?</th>
          <th class="min-w-[60px]">Pon</th>
          <th class="min-w-[60px]">Tor</th>
          <th class="min-w-[60px]">Sre</th>
          <th class="min-w-[60px]">Čet</th>
          <th class="min-w-[60px]">Pet</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="data && filteredStudents" v-for="(pupil, index) in filteredStudents.sort(sorting)" :key="pupil.id">
          <tr v-if="index == 0 || pupil.clazz != filteredStudents.sort(sorting)[index - 1].clazz" class="text-center">
            <td colspan="9" class="text-lg bg-sandy-light">
              {{pupil.clazz}}
            </td>
          </tr>
          <tr @click="pupilClicked(pupil)" class="hover:bg-my-blue-light cursor-pointer" :class="{ 'animate-flash-sandy': pupilHighlighted && pupil.id == pupilHighlighted.id }">
            <td class="pl-3">{{ pupil.name }}</td>
            <td></td>
            <td class="text-center p-0 text-xl text-my-green"><span v-if="pupil.leavesAlone">✓</span></td>
            <td>{{ time(pupil.departure.monday) }}</td>
            <td>{{ time(pupil.departure.tuesday) }}</td>
            <td>{{ time(pupil.departure.wednesday) }}</td>
            <td>{{ time(pupil.departure.thursday) }}</td>
            <td>{{ time(pupil.departure.friday) }}</td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
  <PupilDialog :pupil="pupilDialog" v-if="pupilDialog"  @close="pupilDialog = null" @updated="(pupil) => pupilUpdated(pupil)" />
</template>

<script lang="ts" setup>

import {useFetch} from "@vueuse/core";
import {stripSeconds} from "@/dateAndTime";
import Header from "@/components/Header.vue";
import {computed, ref} from "vue";
import PupilDialog from "./PupilEditDialog.vue";

const time = stripSeconds;

/** Fetch the initial list of students */
const { isFetching, error, data } = useFetch<dto.PupilDTO[]>('/backoffice/pupils').get().json()

/** The filter that filters pupils and classes by name */
const nameFilter = ref('')

/** The filter, split into parts */
const filterParts = computed(() => nameFilter.value.split(' ').map( (p) => p.toUpperCase()))

/** Students matching any of the filter parts */
const filteredStudents = computed<dto.PupilDTO[]>(() => {
  if (!nameFilter.value) return data.value;
  else {
    return data.value.filter((pup: dto.PupilDTO) => {
      return filterParts.value.some(filterPart =>
        pup.clazz.toUpperCase().indexOf(filterPart) >= 0 || pup.name.toUpperCase().indexOf(filterPart) >= 0
      )
    })
  }
})

/** A sorting algorithm for the pupils */
const sorting = ref((pup1: dto.PupilDTO, pup2: dto.PupilDTO) => {
  return pup1.clazz.localeCompare(pup2.clazz) || pup1.name.localeCompare(pup2.name)
})


// Dialog interaction
const pupilDialog = ref<dto.PupilDTO | null>(null)
const pupilHighlighted = ref<dto.PupilDTO | null>(null)

function newPupil() {
  pupilClicked({
    id: 0,
    name: '',
    clazz: '',
    departure: {
      friday: null,
      monday: null,
      thursday: null,
      tuesday: null,
      wednesday: null,
    },
    leavesAlone: false,
    plannedDepartures: [],
  });
}

function pupilClicked(pupil: dto.PupilDTO) {
  pupilDialog.value = pupil;
  pupilHighlighted.value = null;
}

function pupilUpdated(pupil: dto.PupilDTO) {
  if (pupilDialog.value!.id) {
    Object.assign(pupilDialog.value, pupil);
  } else {
    data.value!.push(pupil);
  }
  pupilHighlighted.value = pupil;
}
</script>