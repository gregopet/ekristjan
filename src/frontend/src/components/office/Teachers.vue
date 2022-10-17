<template>
  <div v-if="error">
    Napaka pri nalaganju podatkov!
  </div>
  <div v-else-if="isFetching">
    Nalagam podatke
  </div>
  <div v-else-if="teachers" class="pt-3">
    <button @click="newTeacher" class="my-2 bg-green-6000 text-black rounded py-1 pl-4 pr-5 font-bold">
      <span class="font-bold">＋</span>
      Učitelj/učiteljica
    </button>

    <hr class="my-4">

    <table class="table mt-1 w-full [&>*>tr>*]:pb-2">
      <thead>
        <tr>
          <th class="text-left pl-3">Ime</th>
          <th class="text-center pl-3">Pisarna</th>
          <th class="text-left">Email naslov</th>
        </tr>
      </thead>
      <tbody>
        <tr
            v-for="teacher in teachers.sort(sorting)"
            @click="teacherClicked(teacher)"
            class="hover:bg-sandy-light cursor-pointer pb-4"
            :class="{
              'animate-flash-sandy': teacherHighlighted && teacher.id === teacherHighlighted.id,
              'text-gray-400': !teacher.enabled
            }"
        >
          <td class="pl-3">{{ teacher.name }}</td>
          <td class="text-center p-0 text-xl text-my-green"><span v-if="teacher.backofficeAccess">✓</span></td>
          <td class="tracking-wide">{{ teacher.email}}</td>
        </tr>
      </tbody>
    </table>

    <TeacherEditDialog v-if="teacherDialog" :teacher="teacherDialog" @close="teacherDialog = null" @updated="(teacher) => teacherUpdated(teacher)" />
  </div>
</template>

<script lang="ts" setup>

import {useFetch} from "@vueuse/core";
import {computed, ref} from "vue";
import TeacherEditDialog from "@/components/office/TeacherEditDialog.vue";

const { isFetching, error, data } = useFetch<dto.PupilDTO[]>('/backoffice/teachers').get().json()
const teachers = computed(() => data.value as dto.TeacherDTO[])

function newTeacher() {
  teacherClicked({
    id: 0,
    name: '',
    email: '',
    enabled: true,
    backofficeAccess: false,
  })
}

/** A sorting algorithm for the pupils */
const sorting = ref((pup1: dto.TeacherDTO, pup2: dto.TeacherDTO) => {
  return pup1.name.localeCompare(pup2.name)
})

// Dialog interaction
const teacherDialog = ref<dto.TeacherDTO | null>(null)
const teacherHighlighted = ref<dto.TeacherDTO | null>(null)
function teacherClicked(teacher: dto.TeacherDTO) {
  teacherDialog.value = teacher;
  teacherHighlighted.value = null;
}

function teacherUpdated(teacher: dto.TeacherDTO) {
  if (teacherDialog.value!.id) {
    Object.assign(teacherDialog.value, teacher);
  } else {
    data.value!.push(teacher);
  }
  teacherHighlighted.value = teacher;
}

</script>