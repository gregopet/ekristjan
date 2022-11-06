<template>
  <div>
    <div @click.self="close()" class="fixed left-0 right-0 top-0 bottom-0 z-20 hidden sm:block sm:opacity-80 sm:bg-gray-300" ref="dialogBackdrop"></div>
    <div class="fixed flex flex-col sm:justify-center left-0 right-0 top-0 bottom-0 z-20 bg-white sm:bg-transparent pointer-events-none" ref="dialogParent">
      <div class="bg-white sm:mx-auto sm:shadow-lg sm:rounded pointer-events-auto">

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

/*
The dialog will render as full page for phones in the below-sm sizes, and as a dialog with backdrop for larger screens.
The detection is made using the backdrop - if it's displayed, we are not on such small screens.
This method may not work too well on iOS devices because they delay zoom events, but it should only be a minor glitch.
The alternative is to prevent user zooming via the meta tag, but we're avoiding this for now for accessibility reasons.
 */
import {onMounted, onUnmounted, ref} from "vue";

const dialogBackdrop = ref<HTMLElement | null>(null);
const dialogParent = ref<HTMLElement | null>(null);

/** Functions we need to call to clean up before elements unmount */
let cleanups: Function[] = []

onMounted(() => {
  const compactDialog = window.getComputedStyle(dialogBackdrop.value!).display == 'none';
  if (compactDialog) {
    // Solving the problem where users zoom in and cannot see the dialog anymore
    // We start by aligning the dialog with the users' visual viewport & then 'prevent' the user from scrolling past
    // the dialog by moving it
    const resizeCallback = (ev: Event | undefined) => {
      if (ev == undefined || dialogParent.value!.offsetTop > window.visualViewport.offsetTop) {
        dialogParent.value!.style.top = `${window.visualViewport.offsetTop}px`;
      }
      dialogParent.value!.style.left = `${window.visualViewport.offsetLeft}px`;
      dialogParent.value!.style.width = `${window.visualViewport.width}px`;
    }
    resizeCallback(undefined);
    window.visualViewport.addEventListener("scroll", resizeCallback);
    window.visualViewport.addEventListener("resize", resizeCallback);
    cleanups.push(() => window.visualViewport.removeEventListener("scroll", resizeCallback))
    cleanups.push(() => window.visualViewport.removeEventListener("resize", resizeCallback))
  }
})

onUnmounted(() => {
  cleanups.forEach(cleanup => cleanup());
})

/** Emits close when dialog wants to be closed */
const emit = defineEmits(['close'])

function close() {
  emit('close');
}

</script>