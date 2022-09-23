<template>
  <Header></Header>
  <div v-if="error">
    Napaka pri nalaganju podatkov!
  </div>
  <div v-else-if="isFetching">
    Nalagam podatke
  </div>
  <div v-else>
    <table class="table">
      <thead>
        <tr>
          <th>Ime</th>
          <th class="text-center">Skupina</th>
          <th colspan="6" class="text-center">Redni odhodi</th>
          <th class="text-center">Izredni odhodi</th>
        </tr>
        <tr class="text-center">
          <th colspan="2">
            <input class="input-sm" placeholder="Išči..." v-model="nameFilter" autofocus>
          </th>
          <td>Sam?</td>
          <td>Pon</td>
          <td>Tor</td>
          <td>Sre</td>
          <td>Čet</td>
          <td>Pet</td>
          <td></td>
        </tr>
      </thead>
      <tbody>
        <tr v-if="data && filteredStudents" v-for="pupil in filteredStudents.sort(sorting)">
          <td>{{ pupil.name }}</td>
          <td class="text-center">{{ pupil.clazz }}</td>
          <td class="text-center p-0 text-2xl text-my-green"><span v-if="pupil.leavesAlone">✓</span></td>
          <td>{{ time(pupil.departure.monday) }}</td>
          <td>{{ time(pupil.departure.tuesday) }}</td>
          <td>{{ time(pupil.departure.wednesday) }}</td>
          <td>{{ time(pupil.departure.thursday) }}</td>
          <td>{{ time(pupil.departure.friday) }}</td>
          <td>
            <div v-for="dep in pupil.plannedDepartures">
              {{ dep.date }}: {{ time(dep.time) }}
            </div>

          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script lang="ts" setup>

import {useFetch} from "@vueuse/core";
import {stripSeconds} from "@/dateAndTime";
import Header from "@/components/Header.vue";
import {computed, ref} from "vue";

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
</script>