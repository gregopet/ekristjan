<template>
  <h1>Prosim, prijavite se v sistem</h1>
  <p> {{message}} </p>
  <form>
    <p>
      <label for="email" autofocus>E-naslov: </label>
      <input type="email" id="email" v-model="email">
    </p>
    <p>
      <label for="password">Geslo: </label>
      <input type="password" id="password" v-model="password">
    </p>
    <p>
      <button @click="login" type="button">Prijava</button>
    </p>
  </form>
  <div>
    Če ste pozabili svoje geslo, <router-link :to="{ name: 'requestPasswordReset'}">kliknite tukaj</router-link>.
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import router from "@/router";
import { login as sendLogin } from '@/security';

const email = ref('')
const password = ref('')
const message = ref('')
const props = defineProps< {
  afterLogin?: string
}>();

/** Tries to log the user in and on success, navigate them either to where they wanted to go or to the default screen */
async function login() {
  const error = await sendLogin(email.value, password.value)
  if (error === null) {
    message.value = ""
    if (props.afterLogin) {
      router.replace(props.afterLogin);
    } else {
      router.replace({ name: 'landing' });
    }
  } else {
    message.value = error
  }
}
</script>