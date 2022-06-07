<script setup lang="ts">
  import Classroom from './components/Classroom.vue';
  import Door from './components/Door.vue';
</script>

<template>
  <main>
    <div v-if="showSelector">
      <h1>
        Kje sem?
      </h1>
      <div>
        <h2>
          <a href="#" @click.prevent="where = 'door'">Na vratih</a>
        </h2>
      </div>
      <div>
        <h2>
          <a href="#" @click.prevent="where = 'classroom'">V učilnici</a>
        </h2>
      </div>
    </div>
    <Classroom v-if="showClassroom"/>
    <Door v-if="showDoor" />

  </main>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  data() {
    return {
      where: ''
    }
  },
  computed: {
    showClassroom(): boolean { return this.where == 'classroom' },
    showDoor(): boolean { return this.where == 'door' },
    showSelector(): boolean { return !this.where }
  }
})

</script>

<style lang="scss">

</style>
