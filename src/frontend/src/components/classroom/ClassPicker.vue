<template>
  <h5 class="classListHeader noClasses sectionHeading" v-if="selected.length == 0">Izberite razrede, katerih odhode spremljate</h5>
  <h5 class="addClassesHeader sectionHeading" v-else>Spremljate odhode razredov</h5>
  <p class="freeClassList">
      <span v-for="cls in allClasses.sort()">
        <span class="class" :class="{ selected: isSelected(cls) }" @click.prevent="toggleClass(cls)">
          {{cls}}
        </span>
        {{ '' }}
      </span>
  </p>
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

<style lang="scss" scoped>
  $padding-left: 20px;

  .sectionHeading {
    font-size: 20px;
    width: 100%;
    padding: 0.5em 0 0 0;
    text-align: center;

    &.classListHeader {
      &.noClasses {
        color: #9b0101;
        font-size: 18px;
        padding: 0.5em;
        text-align: center;
      }
    }
  }
  .freeClassList {
    padding: 5px $padding-left;
    line-height: 50px;
    font-size: 24px;
    text-align: center;

    .class {
      padding: 15px;
      cursor: pointer;

      opacity: 50%;
      text-decoration: underline;
      &.selected {
        opacity: 100%;
        font-weight: bold;
        text-decoration: none;
      }
    }
  }
</style>