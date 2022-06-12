<template>
  <div class="all">
    <div class="settings">
      <h5>Prisotni učenci:</h5>
      <ul class="classList">
          <li v-for="cls in selectedClasses">
              {{ cls }}:
                  <span v-for="(pupil, idx) in pupilsHere(cls)">
                      <span v-if="idx > 0">, </span>
                      {{ pupil.name }}
                  </span>
                  <a @click.prevent="removeClass(cls)" href="#"> (odstrani razred)</a>
          </li>
      </ul>

      <h5>Dodaj še učence iz razreda:</h5>
      <ul class="freeClassList">
          <li v-for="cls in nonSelectedClasses">
              <a @click.prevent="selectClass(cls)" href="#">{{ cls }}</a>
          </li>
      </ul>
    </div>
    <div class="departures">
      <TransitionGroup tag="ul" name="departures">
        <li v-for="departure in calledPupils" :style="{ ...departure.color }" @click="removeCalledPupil(departure)" :key="departure.random">
          <span class="name">
            {{ departure.pupil.name }}
          </span>
          <span class="time">
            {{ formatDate(departure.atTime) }}
          </span>
        </li>
      </TransitionGroup>
    </div>
  </div>
</template>

<script setup lang="ts">
import {computed, defineComponent, onMounted, ref} from "vue";
import fakestate, { type Data, type Pupil } from "../data";
import { type SendPupil, eventBus } from '../events';
import { format } from 'date-fns';
import { type Palette, ColorChoice } from "@/components/palette";
import notificationSound from'../assets/message-ringtone-magic.mp3';
import { removePupil } from "../data";
import { notification } from "../swInterop";
import logo from "../assets/francetabevka.jpg"

onMounted(() => {
  eventBus.on("SendPupil", (ev) => {
    callPupil(ev as SendPupil);
  })
})

// Formatting
function formatDate(date: Date) {
  return format(date, "H.mm");
}

// Class selection
const classes = ref(["1A"]);
const selectedClasses = computed(() => Array.from(fakestate.classes.keys()).filter( (cls) => classes.value.indexOf(cls) >= 0))
const nonSelectedClasses = computed( () => Array.from(fakestate.classes.keys()).filter( (cls) => classes.value.indexOf(cls) < 0))
function selectClass(forClass: string) { classes.value.push(forClass) }
function removeClass(forClass: string) {
  const idx = classes.value.findIndex( (cl) => cl === forClass);
  if (idx >= 0) classes.value.splice(idx, 1);
}
function pupilsHere(forClass: string): Pupil[] {
  return fakestate.classes.get(forClass)!.pupils;
}

// Pupil calls
interface SendPupilUI extends SendPupil {
  atTime: Date,
  color: Palette,
  random: number,
}
const colors = new ColorChoice();
const calledPupils = ref([] as SendPupilUI[])

function callPupil(pupil: SendPupil) {
  if (selectedClasses.value.indexOf(pupil.fromClass) >= 0) {
    calledPupils.value.unshift({
      ...pupil,
      atTime: new Date(),
      color: colors.nextColor(),
      random: Math.random(),
    });
    if (("Notification" in window) && Notification.permission === "granted") {
      const title = `${pupil.pupil.name} - ${pupil.fromClass}`
      notification(title, {
        icon: logo,
        renotify: true,
        tag: title,
        body: "odhod domov"
      })
    } else {
      const audio = new Audio(notificationSound);
      audio.play();
      if (window.navigator?.vibrate) {
        window.navigator.vibrate(600);
      }
    }
  } else {
    console.log("Pupil not from this class")
  }
}

function removeCalledPupil(pupil: SendPupil) {
  removePupil(pupil.fromClass, pupil.pupil);
  while(true) {
    const idx = calledPupils.value.findIndex((p) => p.fromClass == pupil.fromClass && p.pupil.name == pupil.pupil.name)
    if (idx >= 0) {
      calledPupils.value.splice(idx, 1);
    } else {
      break;
    }
  }
}
</script>

<style scoped lang="scss">
  .all {
    display: flex;


    .settings {
      flex-basis: 350px;
      .classList {
        list-style-type: none;
      }
    }
    .departures {
      flex-grow: 1;
      ul {
        list-style-type: none;
        li {
          display: flex;
          justify-content: space-between;
          padding: 0.5em 0.25em;

          font-size: 20px;
          background-color: red;
          color: white;
          border: 2px solid white;
        }
      }
    }
  }

  // <TransitionGroup>
  .departures-move,
  .departures-enter-active,
  .departures-leave-active {
    transition: all 1s ease;
  }
  .departures-enter-from,
  .departures-leave-to {
    opacity: 0;
    transform: translateX(30px);
  }
  .departures-leave-active { position: absolute; }

</style>