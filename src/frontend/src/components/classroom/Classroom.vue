<template>
  <Header />
  <ClassPicker v-model:selected="selectedClasses" class="mt-5" />
  <PupilList :selectedClasses="selectedClasses" />
</template>

<script setup lang="ts">
import { onMounted, ref, watch} from "vue";
import { pupilsFromClass } from "../../data";
import { eventBus } from '../../events';
import { type Palette, ColorChoice } from "@/components/palette";
import notificationSound from '../../assets/message-ringtone-magic.mp3';
import { notification } from "../../swInterop";
import logo from "../../assets/francetabevka.jpg"
import {getServerKey, subscribeOnClient, subscribeOnServer} from "@/subscription";
import {usePermission, useWebNotification} from "@vueuse/core";
import ClassPicker from './ClassPicker.vue';
import PupilList from './PupilList.vue';
import Header from '../Header.vue';

const fromClass = pupilsFromClass

onMounted(() => {
  eventBus.on("SendPupil", (ev) => {
    callPupil(ev as dto.Pupil);
  })
})

// Class selection
const selectedClasses = ref<string[]>([]);
watch(selectedClasses, async (newValues) =>  {
  await pushClassSubscriptions();
})

// TODO: ask about these two?
const { isSupported, show } = useWebNotification()
const pushAccessInitial = usePermission("push")

async function pushClassSubscriptions() {
  const key = await getServerKey()
  const subscription = await subscribeOnClient(key)
  await subscribeOnServer(subscription, selectedClasses.value);
}

// Pupil calls
interface SendPupilUI extends dto.Pupil {
  atTime: Date,
  color: Palette,
  random: number,
}
const colors = new ColorChoice();
const calledPupils = ref([] as SendPupilUI[])


function callPupil(pupil: dto.Pupil) {
  console.log("Call pupil", pupil)
  if (selectedClasses.value.indexOf(pupil.fromClass) >= 0) {
    console.log("Notifying..", pupil)
    calledPupils.value.unshift({
      ...pupil,
      atTime: new Date(),
      color: colors.nextColor(),
      random: Math.random(),
    });
    audioHapticFeedback();
    /*if (("Notification" in window) && Notification.permission === "granted") {
      const title = `${pupil.name} - ${pupil.fromClass}`
      notification(title, {
        icon: logo,
        renotify: true,
        tag: title,
        body: "odhod domov",
        silent: false,
      })
    }*/
  } else {
    console.log("Pupil not from this class")
  }
}

function audioHapticFeedback() {
  const audio = new Audio(notificationSound);
  audio.play();
  if (window.navigator?.vibrate) {
    window.navigator.vibrate(600);
  }
}



function removeCalledPupil(pupil: dto.Pupil) {
  while(true) {
    const idx = calledPupils.value.findIndex((p) => p.fromClass == pupil.fromClass && p.name == pupil.name)
    if (idx >= 0) {
      calledPupils.value.splice(idx, 1);
    } else {
      break;
    }
  }
}
</script>