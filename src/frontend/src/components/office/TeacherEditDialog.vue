<template>
  <BackofficeDialog ref="dialog" @close="close">
    <template v-slot:heading>
      <h3 v-if="props.teacher.id">{{ props.teacher.name }}</h3>
      <h3 v-else>Nov učitelj</h3>
    </template>

    <div class="flex gap-10">
      <div>
        <div class="grid grid-cols-[auto_auto] h-[fit-content] items-center justify-items-end gap-2">
          <label class="label" for="name">Ime:</label>
          <input class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" v-model="teacher.name" id="name">

          <label class="label" for="email">Email:</label>
          <input class="h-8 border border-slate-300 rounded-md text-sm shadow-sm focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500 px-1" v-model="teacher.email" id="email">

          <label class="label cursor-pointer" for="enabled">Dostop:</label>
          <label class="h-8 border border-slate-300 bg-slate-50 justify-self-stretch rounded-md text-center flex justify-center items-center">
            <input type="checkbox" v-model="teacher.enabled" id="enabled" class="mx-0.5">
          </label>

          <label class="label cursor-pointer" for="backofficeAccess">Pisarna:</label>
          <label class="h-8 border border-slate-300 bg-slate-50 justify-self-stretch rounded-md text-center flex justify-center items-center">
            <input type="checkbox" v-model="teacher.backofficeAccess" id="backofficeAccess" class="mx-0.5">
          </label>

        </div>

      </div>
    </div>

    <div>
      <p class="text-center space-y-3 md:space-x-3 mb-3">
        <button class="bg-my-blue text-white rounded p-2 w-[150px]" @click="save()">Shrani</button>
        <button @click="close()" class="bg-sandy text-white rounded p-2 w-[150px]">Zapri</button>
      </p>
    </div>

  </BackofficeDialog>
</template>

<script lang="ts" setup>
import BackofficeDialog from "@/components/office/BackofficeDialog.vue";
import {ref} from "vue";
import {useToast} from "vue-toastification";

const props = defineProps<{
  teacher: dto.TeacherDTO,
}>()

/** Emits close when dialog wants to be closed */
const emit = defineEmits(['close', 'updated'])

/** A copy of the pupil that we are editing, if we save it the properties will get saved back */
const teacher = ref({...props.teacher});


function close() {
  emit('close');
}

function updated() {
  emit('updated', teacher.value);
  close();
}

const toast = useToast();

async function save() {
  const response = await fetch("/backoffice/teacher", {
    method: "POST",
    body: JSON.stringify(teacher.value)
  })
  if (response.ok) {
    if (teacher.value.id) {
      toast.success("Shranjevanje uspešno", {timeout: 1500});
    } else {
      const newItemId = await response.text()
      teacher.value.id = parseInt(newItemId, 10);
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