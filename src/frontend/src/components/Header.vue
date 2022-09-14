<template>
  <header class="flex justify-center items-center w-full font-logo text-background-beige bg-background-green">
    <router-link v-if="back" :to="back" class="absolute h-full left-0 ml-4 w-5"></router-link>
    <h1 class="my-1 text-xl tracking-wider">eKristjan</h1>
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
  a {
    background-size: 80%;
    background: url('../assets/Back_Arrow.svg') no-repeat center center;
  }
}
</style>