<template>
  <PublicLayout>
      <p v-if="message" class="mb-5 p-2 bg-red-100 text-red-700 border-red-50 border">
        {{message}}
      </p>
      <form>
        <div class="space-y-3">

          <label class="block">
            <span class="block text-sm font-medium text-slate-700">E-naslov</span>
            <input type="email" v-model="email" v-on:keyup.enter="login" autofocus class="
              mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
              focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500
            ">
          </label>

          <label class="block">
            <span class="block text-sm font-medium text-slate-700">Geslo</span>
            <input type="password" v-model="password" v-on:keyup.enter="login"  class="
              mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
              focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500
            ">
          </label>

          <div class="text-gray-500">
            Če ste pozabili svoje geslo,
            <router-link :to="{ name: 'requestPasswordReset'}" class="underline text-blue-700">ga lahko ponastavite tukaj</router-link>.
          </div>
        </div>

          <button @click="login" type="button" class="mt-6 bg-blue-600 block w-full text-white p-2 rounded">Prijava</button>
      </form>
  </PublicLayout>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import router from "@/router";
import { loggedIn } from '@/main';
import {watchOnce} from "@vueuse/core";
import PublicLayout from './PublicLayout.vue';

const email = ref('')
const password = ref('')
const message = ref('')
const props = defineProps< {
  afterLogin?: string
}>();

/** Tries to log the user in and on success, navigate them either to where they wanted to go or to the default screen */
async function login() {
  try {
    const login = await fetch("/security/login", {
      method: "POST",
      body: JSON.stringify({email: email.value, password: password.value})
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
          router.replace({name: 'landing'});
        }
      }

      if (loggedIn.value) {
        forward()
      } else {
        watchOnce(loggedIn, forward)
      }

    } else if (login.status == 401) {
      message.value = "Napačno geslo";
    } else if (login.status == 404) {
      message.value = "Neznan uporabnik";
    } else {
      message.value = "Prišlo je do neznane napake - preverite internetno povezavo ali pa poizkusite kasneje!";
    }
  } catch (err) {
    message.value = "Napaka pri povezavi"
  }
}
</script>