<template>
  <div>
    <h5 class="text-lg text-center text-red-700" v-if="selected.length == 0">Izberite skupine, katerih odhode spremljate</h5>
    <h5 v-else @click="resetTimer()" class="text-xl ml-5">
      <span class="cursor-pointer">{{ groupsPlural }}</span>
    </h5>

    <Transition>
      <p v-if="showClassSelector" class="text-2xl mt-3 flex flex-wrap gap-x-10 gap-y-4 justify-center">
          <span v-for="cls in allClasses.sort()">
            <span class="cursor-pointer opacity-50" :class="{ 'opacity-100 font-bold': isSelected(cls) }" @click.prevent="toggleClass(cls)">
              {{cls}}
            </span>
            {{ '' }}
          </span>
      </p>
    </Transition>
  </div>
</template>

<style scoped>
.v-enter-active, .v-leave-active {  transition: opacity 0.5s ease; }
.v-enter-from, .v-leave-to { opacity: 0; }
</style>

<script lang="ts" setup>

/*
  Presents a list of all classes and allows users to toggle them, emitting changes.
 */
import {classes} from "@/data";
import {computed, ref, watch} from "vue";
import {useInterval} from "@vueuse/core";

const SELECTOR_SHOW_FOR_MS = 5000

const showClassSelector = ref(true)
const counter = useInterval(500)
const lastClassroomSelectorClick = ref(new Date())
watch(counter, () => {
  showClassSelector.value = selectedClasses.value.length == 0 || lastClassroomSelectorClick.value.getTime() + SELECTOR_SHOW_FOR_MS > new Date().getTime()
})

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

const selectedClasses = computed(() => {
  return classes.value.filter(isSelected)
})

const groupsPlural = computed(() => {
  const selectedCount = selectedClasses.value.length;
  const groups = selectedClasses.value.sort()
  switch (selectedCount) {
    case 1: return `Spremljate učence ${groups[0]}`;
    case 2: return `Spremljate učence ${groups.join(' in ')}`;
    default: return `Učenci ${groups.join(', ')}`;
  }
})

/** Toggles whether this class is selected or not */
function toggleClass(clazz: string) {
  const newClasses = [...props.selected]
  const index = newClasses.indexOf(clazz);
  if (index >= 0) {
    newClasses.splice(index, 1)
  } else {
    newClasses.push(clazz)
  }
  emits('update:selected', newClasses);
  resetTimer();
}

function resetTimer() {
  lastClassroomSelectorClick.value = new Date();
  showClassSelector.value = true;
}
</script>