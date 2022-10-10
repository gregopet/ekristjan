<template>
  <div v-if="error">
    Napaka pri nalaganju podatkov!
  </div>
  <div v-else-if="isFetching">
    Nalagam podatke
  </div>
  <div v-else class="pt-3">
    <button @click="newPupil" class="my-2 bg-green-6000 text-black rounded py-1 pl-4 pr-5 font-bold">
      <span class="font-bold">＋</span>
      Učenec/učenka
    </button>

    <hr class="my-4">

    <table class="table mt-1">
      <thead>
        <tr>
          <th class="text-left pl-3">Ime</th>
          <th class="min-w-[20px]"></th>
          <th colspan="6" class="text-center">Redni odhodi</th>
        </tr>
        <tr class="text-center ">
          <th class="pl-3 pb-2i font-normal">
            <input type="search" class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" placeholder="Išči..." v-model="nameFilter" ref="autofocus">
          </th>
          <th></th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Sam</th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Pon</th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Tor</th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Sre</th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Čet</th>
          <th class="min-w-[60px] pb-2 font-normal text-gray-700 uppercase">Pet</th>
        </tr>
      </thead>
      <tbody>
      <template v-if="data && filteredStudents" v-for="(pupil, index) in filteredStudents.sort(sorting)" :key="pupil.id">
        <template v-if="index == 0 || pupil.clazz != filteredStudents.sort(sorting)[index - 1].clazz">
          <tr class="text-center">
            <td class="pt-4 pb-1">
              <span class="text-2xl  p-2 w-full block text-center">{{pupil.clazz}}</span>
            </td>
          </tr>
          <tr>
            <td colspan="9" >
              <span class="bg-gradient-to-r from-sandy to-white h-[4px] w-full block mb-4">&nbsp;</span>
            </td>
          </tr>
        </template>
        <tr @click="pupilClicked(pupil)" class="hover:bg-sandy-light cursor-pointer pb-4" :class="{ 'animate-flash-sandy': pupilHighlighted && pupil.id == pupilHighlighted.id }">
          <td class="pb-2 pl-3">{{ pupil.name }}</td>
          <td class="pb-2 "></td>
          <td class="pb-2 text-center p-0 text-xl text-my-green"><span v-if="pupil.leavesAlone">✓</span></td>
          <td class="pb-2 ">{{ time(pupil.departure.monday) }}</td>
          <td class="pb-2 ">{{ time(pupil.departure.tuesday) }}</td>
          <td class="pb-2 ">{{ time(pupil.departure.wednesday) }}</td>
          <td class="pb-2 ">{{ time(pupil.departure.thursday) }}</td>
          <td class="pb-2 ">{{ time(pupil.departure.friday) }}</td>
        </tr>
      </template>
      </tbody>
    </table>
  </div>
  <PupilDialog :pupil="pupilDialog" v-if="pupilDialog"  @close="pupilDialog = null" @updated="(pupil) => pupilUpdated(pupil)"  :existing-groups="allGroups" />
</template>

<script lang="ts" setup>

import {useFetch} from "@vueuse/core";
import {stripSeconds} from "@/dateAndTime";
import {computed, nextTick, onMounted, ref, watch} from "vue";
import PupilDialog from "./PupilEditDialog.vue";

const time = stripSeconds;

/** The element to focus as soon as data comes in */
const autofocus = ref<HTMLElement | null>(null);

/** Fetch the initial list of students */
const { isFetching, error, data } = useFetch<dto.PupilDTO[]>('/backoffice/pupils').get().json()

// Focus the search element once load completes
watch(isFetching, (newVal) => {
  nextTick( () => {
    if (newVal == false && autofocus.value) {
      nextTick(() => autofocus.value!.focus());
    }
  })
})

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
const allGroups = computed(() => uniqueArray(
    data.value!.map( (pup: dto.PupilDTO) => pup.clazz) as string[]
));

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

/** Leaves only unique elements in the array */
function uniqueArray<T>(input: Array<T>): Array<T> {
  return Array.from(new Set(input));
}
</script>