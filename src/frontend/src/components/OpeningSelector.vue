<template>
  <main>
      <h1>
        Kje sem?
      </h1>
      <div>
        <h2>
          <router-link to='/vrata'>Na vratih</router-link>
        </h2>
      </div>
      <div>
        <h2>
          <router-link to='/ucilnica'>V učilnici</router-link>
        </h2>
      </div>
      <router-view></router-view>
  </main>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({})

</script>

<style lang="scss">

</style>
