<template>
  <Header :back="{ name: 'landing'}"/>

  <DoorSummonedPupils></DoorSummonedPupils>

  <h5 class="classListHeader noClasses sectionHeading">Pokliči učenca iz:</h5>
  <ul>
    <li v-for="clazz in allClasses.sort()">
      <router-link :to="'/prijavljen/vrata/' + clazz">{{clazz}}</router-link>
    </li>
  </ul>
</template>

<script setup lang="ts">
import {classes} from "../../data";
import Header from '../Header.vue';
import DoorSummonedPupils from "./DoorSummonedPupils.vue";

const allClasses = classes;
</script>


<style lang="scss" scoped>

$padding-left: 20px;

.sectionHeading {
  font-size: 20px;
  width: 100%;
  padding: 0 0 0 $padding-left;
  &:first-child {
    padding-top: 0.5em;
  }
}

ul {
  display: block;
  li {
    display: inline-block;
    padding: 15px;
    line-height: 50px;
    font-size: 24px;
    text-align: center;
    font-color: black;
    cursor: pointer;
    a { color: black; text-decoration: none; }
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
