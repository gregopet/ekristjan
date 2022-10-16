<template>
  <div>
    <div @click="close()" class="fixed left-0 right-0 top-0 bottom-0 bg-gray-300 opacity-80 z-20" id="sss"></div>
    <div class="fixed flex flex-col justify-center left-0 right-0 top-0 bottom-0 z-20">
      <div class="bg-white mx-auto shadow-lg rounded">

        <!-- top part -->
        <div class="flex justify-between items-center border-b border-red-50 px-3 mb-4">
          <div>
            <slot name="heading"></slot>
          </div>
          <div @click="close" class="p-2">
            <div class="rotate-45 text-2xl cursor-pointer">+</div>
          </div>
        </div>

        <!-- main content -->
        <div class="px-2">
          <slot />
        </div>

      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
/** Emits close when dialog wants to be closed */
const emit = defineEmits(['close'])

function close() {
  emit('close');
}

</script>