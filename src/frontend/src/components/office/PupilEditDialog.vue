<template>
  <div>
    <BackofficeDialog @close="close">
      <template v-slot:heading>
        <h3 v-if="props.pupil.id">{{ props.pupil.givenName }} {{ props.pupil.familyName }}, {{ props.pupil.clazz }}</h3>
        <h3 v-else>Nov učenec</h3>
      </template>

      <div class="flex gap-10">

        <div class="grid grid-cols-[auto_auto] h-[fit-content] items-center justify-items-end gap-2">
          <label class="label" for="givenName">Ime:</label>
          <input class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" v-model="pupil.givenName" id="givenName">

          <label class="label" for="familyName">Priimek:</label>
          <input class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" v-model="pupil.familyName" id="familyName">

          <label class="label" for="group">Skupina:</label>
          <input class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" v-model="pupil.clazz" id="group" list="group-list">

          <datalist id="group-list">
            <option :value="group" v-for="group in props.existingGroups"></option>
          </datalist>

          <label class="label cursor-pointer" for="leavesAlone">Odhaja sam(a):</label>
          <label class="h-8 border border-slate-300 bg-slate-50 justify-self-stretch rounded-md text-center flex justify-center items-center">
            <input type="checkbox" v-model="pupil.leavesAlone" id="leavesAlone" class="mx-0.5">
          </label>
        </div>

        <div class="grid grid-cols-[auto_auto] h-[fit-content] items-center justify-items-end gap-2">
          <label class="label" for="mon">Ponedeljek:</label>
          <input class="border border-slate-300 rounded-md h-8" v-model="pupil.departure.monday" id="mon" type="time">
          <label class="label" for="tue">Torek:</label>
          <input class="border border-slate-300 rounded-md h-8" v-model="pupil.departure.tuesday" id="tue" type="time">
          <label class="label" for="wed">Sreda:</label>
          <input class="border border-slate-300 rounded-md h-8" v-model="pupil.departure.wednesday" id="wed" type="time">
          <label class="label" for="thu">Četrtek:</label>
          <input class="border border-slate-300 rounded-md h-8" v-model="pupil.departure.thursday" id="thu" type="time">
          <label class="label" for="fri">Petek:</label>
          <input class="border border-slate-300 rounded-md h-8" v-model="pupil.departure.friday" id="fri" type="time">
        </div>

      </div>

      <div>
        <p class="text-center space-y-3 md:space-x-3 mb-3">
          <button class="bg-my-blue text-white rounded p-2 w-[150px]" @click="save">Shrani</button>
          <button @click="close" class="bg-sandy text-white rounded p-2 w-[150px]">Zapri</button>
        </p>

      </div>
    </BackofficeDialog>
  </div>
</template>

<script lang="ts" setup>

import {date2Time, stripSeconds} from "@/dateAndTime";
import {ref} from "vue";
import Toast, {useToast} from "vue-toastification";
import BackofficeDialog from "@/components/office/BackofficeDialog.vue";

const formatDate = date2Time;
const formatTime = stripSeconds;

const props = defineProps<{
  pupil: dto.PupilDTO,
  existingGroups: string[],
}>()

/** A copy of the pupil that we are editing, if we save it the properties will get saved back */
const pupil = ref({...props.pupil});

function mounted() {

}

/** Emits close when dialog wants to be closed */
const emit = defineEmits(['close', 'updated'])


function close() {
  emit('close');
}

function updated() {
  emit('updated', pupil.value);
  close();
}

const toast = useToast();

async function save() {
  const response = await fetch("/backoffice/pupil", {
    method: "POST",
    body: JSON.stringify(pupil.value)
  })
  if (response.ok) {
    if (pupil.value.id) {
      toast.success("Shranjevanje uspešno", {timeout: 1500});
    } else {
      const newItemId = await response.text()
      pupil.value.id = parseInt(newItemId, 10);
      toast.success("Dodajanje uspešno", {timeout: 1500});
    }
    updated();
  } else {
    const toastId = toast.error("Napaka pri shranjevanju!");
    const body = await response.text()
    if (body) {
      toast.update(toastId, {content: "Napaka pri shranjevanju: " + body})
    }
  }
}



</script>