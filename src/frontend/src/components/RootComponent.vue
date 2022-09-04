<template>
  <router-view></router-view>
</template>

<script lang="ts" setup>
import { useWakeLock } from "@vueuse/core";
import { useRegisterSW } from 'virtual:pwa-register/vue'
import {watch} from "vue";
import { loggedIn } from '@/main';

const login = loggedIn

// Keep screen turned on because we want those notifications!
const { isSupported, isActive, request, release } = useWakeLock()
request('screen')
</script>

<style lang="scss" scoped>

</style>