<template>
    <Header />

    <DoorSummonedPupils></DoorSummonedPupils>

    <h5 class="text-xl pl-5 pb-4">Pokliči učenca iz:</h5>
    <ul class="flex flex-wrap gap-6 justify-center px-5">
      <li v-for="clazz in allClasses.sort()" class="text-2xl text-black pointer">
        <router-link :to="'/prijavljen/vrata/' + clazz">{{clazz}}</router-link>
      </li>
    </ul>
</template>

<script setup lang="ts">
import {classes} from "../../data";
import Header from '../Header.vue';
import DoorSummonedPupils from "./DoorSummonedPupils.vue";
import '../LoggedInLayout.vue'
import LoggedInLayout from "@/components/LoggedInLayout.vue";

const allClasses = classes;
</script>