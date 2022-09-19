<template>
  <PublicLayout>

  <div v-if="resetSuccessful !== null">
    <div v-if="resetSuccessful" class="text-lg">
      Geslo je bilo uspešno ponastavljeno!
      Nadaljujete lahko <router-link :to="{ name: 'landing'}" class="text-blue-700 underline">tukaj</router-link>.
    </div>
    <div v-else class="mb-5 p-2 bg-red-100 text-red-700 border-red-50 border">Pri ponastavitvi je prišlo do težav - prosimo, poizkusite znova!</div>
  </div>
  <div v-else>

    <p v-if="validityCheckReq.isFinished && validityCheckReq.error.value" class="mb-5 p-2 bg-red-100 text-red-700 border-red-50 border">
      Prišlo je do težave pri preverjanju istovetnosti vašega zahtevka - prosimo, preverite delovanje vaše internetne
      povezave ali pa začnite postopek znova!
    </p>

    <div v-else>
      <h1 class="text-xl mb-5">Ponastavitev gesla</h1>

      <form>
        <div class="space-y-3">
          <label class="block">
            <span class="block text-sm font-medium text-slate-700">Vnesite novo geslo</span>
            <input type="password" v-model="password" class="
              mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
              focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500
            ">
          </label>

          <label class="block">
            <span class="block text-sm font-medium text-slate-700">Ponovite geslo</span>
            <input type="password"  v-model="passwordRepeat" class="header
              mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
              focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500
            ">
          </label>
        </div>

        <button @click.prevent="resetPassword" type="button" class="mt-6 bg-blue-600 block w-full text-white p-2 rounded">Ponastavi</button>

      </form>
    </div>
  </div>
  </PublicLayout>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import {useFetch} from "@vueuse/core";
import PublicLayout from '../PublicLayout.vue';
const route = useRoute()

const password = ref('')
const passwordRepeat = ref('')
const resetToken = route.params.token
const validityCheckReq = useFetch("/security/check-password-reset", { headers: authHeader() }, {})
const resetSuccessful = ref<null | boolean>(null)

async function resetPassword() {
  if (password.value == passwordRepeat.value) {
    const req = await fetch("/security/submit-password-reset", {
      method: 'POST',
      headers: authHeader(),
      body: JSON.stringify({ password: password.value })
    })
    resetSuccessful.value = req.ok
  } else {
    console.log("Passwords don't match")
  }
}

function authHeader(): Record<string, string> {
    return { Authorization: `Bearer ${resetToken}` }
}

</script>