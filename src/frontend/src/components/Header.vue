<template>
  <header>
    <router-link v-if="back" :to="back"></router-link>
    <span v-else></span>
    <h1>eKristjan</h1>
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
  width: 100%;
  height: 2.5em;
  line-height: 2em;
  background: #2f3a35;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &>* { opacity: 94% }

  a {
    margin-left: 3%;
    font-family: sans-serif;
    color: white;
    text-decoration: none;
    width: 20px;
    height: 100%;
    background-size: 80%;
    background: url('../assets/Back_Arrow.svg') no-repeat center center;
  }

  h1 {
    color: white;
    font-size: 20px;
    display: inline;
    font-family: RubikMarkerHatch;
    margin-right: 30%;
  }
}
</style>