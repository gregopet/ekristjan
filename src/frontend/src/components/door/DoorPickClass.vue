<template>
  <LoggedInLayout>
    <Header :back="{ name: 'landing'}"/>

    <DoorSummonedPupils></DoorSummonedPupils>

    <h5 class="text-xl pl-5">Pokliči učenca iz:</h5>
    <ul class="block">
      <li v-for="clazz in allClasses.sort()" class="inline-block p-4 text-2xl text-black pointer">
        <router-link :to="'/prijavljen/vrata/' + clazz">{{clazz}}</router-link>
      </li>
    </ul>
  </LoggedInLayout>
</template>

<script setup lang="ts">
import {classes} from "../../data";
import Header from '../Header.vue';
import DoorSummonedPupils from "./DoorSummonedPupils.vue";
import '../LoggedInLayout.vue'
import LoggedInLayout from "@/components/LoggedInLayout.vue";

const allClasses = classes;
</script>