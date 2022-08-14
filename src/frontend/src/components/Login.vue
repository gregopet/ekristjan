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
import { loggedIn } from '@/main';
import {watchOnce} from "@vueuse/core";

const email = ref('')
const password = ref('')
const message = ref('')
const props = defineProps< {
  afterLogin?: string
}>();

/** Tries to log the user in and on success, navigate them either to where they wanted to go or to the default screen */
async function login() {
  const login = await fetch("/security/login", {
    method: "POST",
    body: JSON.stringify({ email: email.value, password: password.value })
  });
  if (login.ok) {
    message.value = ""
    // This is a bit hackish unfortunately - service worker sends login event but we can't easily watch when the event will
    // be delivered. So we wait until value of 'loggedIn' changes to know login had succeeded.
    // The proper workaround will use https://developer.mozilla.org/en-US/docs/Web/API/Channel_Messaging_API
    // where we can wait for a reply but for now this will have to do.
    function forward() {
      if (props.afterLogin) {
        router.replace(props.afterLogin);
      } else {
        router.replace({ name: 'landing' });
      }
    }
    if (loggedIn.value) {
      forward()
    } else {
      watchOnce(loggedIn, forward)
    }

  } else if (login.status == 401) {
    message.value =  "Napačno geslo";
  } else if (login.status == 404) {
    message.value =  "Neznan uporabnik";
  } else {
    message.value = "Prišlo je do napake";
  }
}
</script>