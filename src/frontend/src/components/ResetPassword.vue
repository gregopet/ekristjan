<template>
  <h1>Ponastavitev gesla</h1>
  <div v-if="resetSuccessful !== null">
    <div v-if="resetSuccessful">Geslo je bilo uspešno ponastavljeno!</div>
    <div v-else>Pri ponastavitvi je prišlo do težav - prosimo, poizkusite znova!</div>
  </div>
  <div v-else>
    <p v-if="validityCheckReq.isFinished && validityCheckReq.error.value">
      Prišlo je do težave pri preverjanju istovetnosti vašega zahtevka - prosimo, preverite delovanje vaše internetne
      povezave ali pa poizkusite postopek znova!
    </p>
    <div v-else>
      <h3>Vnesite novo geslo:</h3>
      <form>
        <p>
          <label for="password" autofocus>Geslo: </label>
          <input type="password" id="password" v-model="password">
        </p>
        <p>
          <label for="passwordRepeat">Ponovi geslo: </label>
          <input type="passwordRepeat" id="passwordRepeat" v-model="passwordRepeat">
        </p>
        <p>
          <button @click="resetPassword" type="button">Ponastavi</button>
        </p>
      </form>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {ref} from "vue";
import {useRoute} from "vue-router";
import {useFetch} from "@vueuse/core";
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