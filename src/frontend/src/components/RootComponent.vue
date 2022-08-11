<template>
  <header>
    <a href="#" @click.prevent="$router.go(-1)"></a>
    <h1>eKristjan</h1>
  </header>
    <router-view></router-view>

</template>

<script lang="ts" setup>
import { useWakeLock } from "@vueuse/core";


// Keep screen turned on because we want those notifications!
const { isSupported, isActive, request, release } = useWakeLock()
request('screen')
</script>

<style lang="scss" scoped>
  header {
    width: 100%;
    height: 2.5em;
    line-height: 2em;
    background: #570505;
    display: flex;
    justify-content: space-between;
    align-items: center;

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