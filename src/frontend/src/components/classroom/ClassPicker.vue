<template>
  <div>
    <h5 class="text-lg text-center text-red-700" v-if="selected.length == 0">Izberite razrede, katerih odhode spremljate</h5>
    <h5 class="text-xl text-center" v-else>Spremljate odhode razredov</h5>

    <p class="text-2xl mt-3 flex flex-wrap gap-x-10 gap-y-4 justify-center">
        <span v-for="cls in allClasses.sort()">
          <span class="cursor-pointer opacity-50" :class="{ 'opacity-100 font-bold': isSelected(cls) }" @click.prevent="toggleClass(cls)">
            {{cls}}
          </span>
          {{ '' }}
        </span>
    </p>
  </div>
</template>

<script lang="ts" setup>

/*
  Presents a list of all classes and allows users to toggle them, emitting changes.
 */
import {classes} from "@/data";


const props = defineProps<{
  selected: string[]
}>()
const emits = defineEmits({
  "update:selected": (classes: string[]) => true,
})
const allClasses = classes

function isSelected(clazz: string) {
  return props.selected.indexOf(clazz) >= 0;
}

/** Toggles whether this class is selected or not */
function toggleClass(clazz: string) {
  const newClasses = [...props.selected]
  const index = newClasses.indexOf(clazz);
  if (index >= 0) {
    newClasses.splice(index, 1)
  } else {
    newClasses.push(clazz)
  }
  emits('update:selected', newClasses)
}
</script>