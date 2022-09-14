<template>
  <header class="flex justify-center items-center w-full font-logo text-background-beige bg-my-green relative p-1">
    <router-link v-if="back" :to="back" class="arrow absolute h-full left-0 ml-4 w-5"></router-link>
    <router-link :to="{name: 'landing'}" class="my-1 text-2xl tracking-wider text-white">eKristjan</router-link>
  </header>
</template>

<script lang="ts" setup>
import type {RouteLocationRaw} from "vue-router";
import type {PropType} from "vue";

const props = defineProps({
  back: {
    type: null as unknown as PropType<RouteLocationRaw>,
    default: null,
  },
})
</script>

<style lang="scss" scoped>
header {
  a.arrow {
    background-size: 80%;
    background: url('../assets/Back_Arrow.svg') no-repeat center center;
  }
}
</style>