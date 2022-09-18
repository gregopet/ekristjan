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
          <th class="text-center">Razred</th>
          <th colspan="6" class="text-center">Redni odhodi</th>
          <th class="text-center">Izredni odhodi</th>
        </tr>
        <tr class="text-center">
          <th colspan="2">
            <input class="input-sm" placeholder="Išči...">
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
        <tr v-if="data" v-for="pupil in data.sort(sorting)">
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
import {ref} from "vue";

const time = stripSeconds;
const { isFetching, error, data } = useFetch<dto.PupilDTO[]>('/backoffice/pupils').get().json()

const sorting = ref((pup1: dto.PupilDTO, pup2: dto.PupilDTO) => {
  return pup1.clazz.localeCompare(pup2.clazz) || pup1.name.localeCompare(pup2.name)
})


</script>