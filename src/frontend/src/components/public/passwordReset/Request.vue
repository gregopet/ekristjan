<template>
  <PublicLayout>
    <header v-if="!requestSent" class="mb-4">
      <h1 class="text-xl">Zahtevek za posodobitev gesla</h1>
    </header>

    <p v-if="error" class="mb-5 p-2 bg-red-100 text-red-700 border-red-50 border">
      {{error}}
    </p>

    <div v-if="requestSent" class="space-y-4">
      <p>
        Na poštni naslov <span class="bg-gray-100 p-1">{{email}}</span> smo vam poslali navodila za posodobitev gesla.
      </p>
      <p>
        Če sporočila po nekaj minutah ne najdete, preverite, da ste naslov vpisali pravilno (poglejte pa tudi v predal za vsiljeno pošto)!
      </p>
    </div>
    <div v-else>
      <p></p>
      <form>
        <label class="block">
          <span class="block text-sm font-medium text-slate-700">Vaš poštni naslov</span>
          <input type="email" v-model="email" v-on:keyup.enter="makeRequest"  class="
              mt-1 block w-full px-3 py-2 bg-white border border-slate-300 rounded-md text-sm shadow-sm placeholder-slate-400
              focus:outline-none focus:border-sky-500 focus:ring-1 focus:ring-sky-500
            ">
        </label>
        <button type="button" @click.prevent="makeRequest" class="mt-8 bg-blue-600 block w-full text-white p-2 rounded">Pošlji zahtevek</button>
      </form>
    </div>
  </PublicLayout>
</template>

<script lang="ts" setup>

import {ref} from "vue";
import PublicLayout from '../PublicLayout.vue';

const email = ref('')
const requestSent = ref(false)
const error = ref('')

async function makeRequest() {
  if (!email.value) return;

  const req = await fetch("/security/request-password-reset", {
    method: "POST",
    body: JSON.stringify({ email: email.value }),
  })

  if (req.ok) {
    error.value = ''
    requestSent.value = true
  } else if (req.status == 404) {
    error.value = "Tega naslova nismo našli - prosimo, preverite če ste ga vpisali pravilno!"
    requestSent.value = false
  } else {
    error.value = (await req.text()) || 'Prišlo je do neznane napake - preverite internetno povezavo ali pa poizkusite kasneje!'
    requestSent.value = false
  }
}

</script>