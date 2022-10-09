<template>
  <Header></Header>

  <div class="pt-1 flex justify-around text-lg mt-1 mb-2 text-my-green font-bold">
    <router-link :to="{name: 'officePupils'}" class="px-3 text-center" active-class="border-b-8 border-double border-sandy">Učenci</router-link>
    <router-link :to="{name: 'officeTeachers'}" class="px-3 text-center" active-class="border-b-8 border-double border-sandy">Učitelji</router-link>
  </div>

  <router-view></router-view>
</template>

<script lang="ts" setup>
import Header from "@/components/Header.vue";
</script>