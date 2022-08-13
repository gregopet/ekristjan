<template>
  <h1>Zahtevek za posodobitev gesla</h1>
  <p v-if="error">{{ error }}</p>
  <div v-if="requestSent">
    <p>
      Na poštni naslov {{email}} smo vam poslali navodila za posodobitev gesla.
      Če sporočila ne najdete, poglejte tudi v predal za vsiljeno pošto!
    </p>
  </div>
  <div v-else>
    <p>Vnesite vaš naslov elektronske pošte in poslali vam bomo</p>
    <form>
      <p>
        <label for="email">Vaš poštni naslov: </label>
        <input type="email" id="email" v-model="email">
      </p>
      <button type="button" @click="makeRequest">Pošlji navodila</button>
    </form>
  </div>
</template>

<script lang="ts" setup>

import {ref} from "vue";

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
    error.value = await req.text()
    requestSent.value = false
  }
}

</script>