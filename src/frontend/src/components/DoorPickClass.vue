<template>
  <p>
    <router-link to="/">Zapri</router-link>
  </p>
  <h4>Izberi razred</h4>
  <ul>
    <li v-for="clazz in allClasses.sort()">
      <router-link :to="'/prijavljen/vrata/' + clazz">{{clazz}}</router-link>
    </li>
  </ul>
</template>

<script setup lang="ts">
import {computed, type Ref, ref} from "vue";
import {classes, pupils, pupilsFromClass} from "../data";
import { refDebounced } from '@vueuse/core';
import {useRouter} from "vue-router";

const allClasses = classes;
const selectedClass = ref('')
const router = useRouter()


function selectClass(clazz: string) {
  router.push(clazz);
}

</script>


<style lang="scss" scoped>

ul.selectPupil {
  list-style-type: none;
  li {
    margin: 0.25em 0.25em;
    ul {
      list-style-type: none;
    }
  }
}

@keyframes blink{
  0% {
    background: white;
  }
  50% {
    background: red;
  }
  100% {
    background: white;
  }
}
</style>
