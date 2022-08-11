<template>
  <div class="all">
    <div class="settings">

      <div v-if="selectedClasses.length">
        <h5 class="classListHeader sectionHeading">Prisotni učenci</h5>
        <ul class="classList">
            <li v-for="cls in selectedClasses.sort()">
                <p class="className">
                  <span>{{ cls }}</span>
                  <small @click="removeClass(cls)">odstrani razred</small>
                </p>
                <p class="pupil" :class="{ departed: pupil.departure, summoned: pupil.summon }" v-for="(pupil, idx) in fromClass(cls)">
                    <span>{{ pupil.pupil.name }}</span>
                    <span v-if="pupil.departure">{{ stripSeconds(pupil.departure.time) }}</span>
                    <span v-else>{{ stripSeconds(pupil.departurePlan.time) }}</span>
                </p>
            </li>
        </ul>
      </div>
      <div v-else>
        <h5 class="classListHeader noClasses sectionHeading">Trenutno ni izbran noben razred!</h5>
      </div>

      <h5 class="addClassesHeader sectionHeading" v-if="nonSelectedClasses.length">Dodaj učence razredov</h5>
      <p class="freeClassList">
        <span v-for="cls in nonSelectedClasses.sort()" @click.prevent="selectClass(cls)">
          {{cls + ' '}}
        </span>
      </p>
    </div>
    <div class="departures">
      <TransitionGroup tag="ul" name="departures">
        <li v-for="departure in calledPupils" :style="{ ...departure.color }" @click="removeCalledPupil(departure)" :key="departure.random">
          <span class="name">
            {{ departure.name }}
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
import {classes, pupils, pupilsFromClass} from "../data";
import { eventBus } from '../events';
import { format } from 'date-fns';
import { type Palette, ColorChoice } from "@/components/palette";
import notificationSound from'../assets/message-ringtone-magic.mp3';
import { notification } from "../swInterop";
import logo from "../assets/francetabevka.jpg"
import {getServerKey, subscribeOnClient, subscribeOnServer} from "@/subscription";
import {usePermission, useWebNotification} from "@vueuse/core";


const fromClass = pupilsFromClass

onMounted(() => {
  eventBus.on("SendPupil", (ev) => {
    callPupil(ev as dto.Pupil);
  })
})

// Formatting
function formatDate(date: Date) {
  return format(date, "H.mm");
}
function stripSeconds(time: string) {
  if (!time) return '';
  return time.substring(0, time.lastIndexOf(":"));
}

// Class selection
const selectedClasses = ref<string[]>([]);
const nonSelectedClasses = computed( () => Array.from(classes.value.filter( (cls) => selectedClasses.value.indexOf(cls) < 0)))
async function selectClass(forClass: string) {
  selectedClasses.value.push(forClass)
  await pushClassSubscriptions()
  // TODO: handle multiple open tabs case
}
async function removeClass(forClass: string) {
  const idx = selectedClasses.value.findIndex( (cl) => cl === forClass);
  if (idx >= 0) {
    selectedClasses.value.splice(idx, 1);
    await pushClassSubscriptions()
    // TODO: handle multiple open tabs case
  }
}

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
    if (("Notification" in window) && Notification.permission === "granted") {
      const title = `${pupil.name} - ${pupil.fromClass}`
      notification(title, {
        icon: logo,
        renotify: true,
        tag: title,
        body: "odhod domov",
        silent: false,
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

<style scoped lang="scss">
  $padding-left: 20px;
  $section-heading-font-size: 15px;

  .all {
    .sectionHeading {
        font-size:15px;
        width: 100%;
        padding: 0.2em $padding-left;

      &.classListHeader {
       &.noClasses {
         color: #9b0101;
         font-size: 18px;
         padding: 0.5em;
         text-align: center;
       }
      }
      &.addClassesHeader { }
    }

    .settings {
      flex-basis: 350px;

      .classList {
        list-style-type: none;
        padding: $section-heading-font-size;
        li {
          padding-bottom: 1.1em;
          .className {
            margin: 0;
            padding: 0;
            display: flex;
            align-items: center;
            font-size: 17px;
            justify-content: space-between;

            small {
              color: #5b626b;
              font-style: italic;
            }
          }

          .pupil {
            display: flex;
            justify-content: space-between;
            &.summoned { color: #ff3f0e; }
            &.departed { text-decoration: line-through; color: #7a828a; }
          }
        }
      }
      .freeClassList {
        padding: 5px $padding-left;
        line-height: 50px;
        font-size: 24px;
        span { padding: 15px }
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