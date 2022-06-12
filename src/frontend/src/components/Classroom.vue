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

<script lang="ts">
import { defineComponent } from "vue";
import fakestate, { type Data, type Pupil } from "../data";
import { type SendPupil, eventBus } from '../events';
import { format } from 'date-fns';
import { type Palette, ColorChoice } from "@/components/palette";
import notificationSound from'../assets/message-ringtone-magic.mp3';
import { removePupil } from "../data";
import { notification } from "../swInterop";
import logo from "../assets/francetabevka.jpg"

interface SendPupilUI extends SendPupil {
  atTime: Date,
  color: Palette,
  random: number,
}

export default defineComponent({
    mounted() {
      eventBus.on("SendPupil", (ev) => {
        this.callPupil(ev as SendPupil);
      })
    },
    data() {
        return {
            classes: ["1A"],
            state: fakestate,
            colors: new ColorChoice(),
            calledPupils: [] as SendPupilUI[],
        }
    },
    computed: {
        selectedClasses(): string[] {
            return Array.from(fakestate.classes.keys()).filter( (cls) => this.classes.indexOf(cls) >= 0);
        },
        nonSelectedClasses(): string[] {
            return Array.from(fakestate.classes.keys()).filter( (cls) => this.classes.indexOf(cls) < 0);
        }
    },
    methods: {
        pupilsHere(forClass: string): Pupil[] {
            return fakestate.classes.get(forClass)!.pupils;
        },
        addClass(forClass: string) {
            this.classes.push(forClass);
        },
        selectClass(forClass: string) {
            this.classes.push(forClass);
        },
        removeClass(forClass: string) {
            const idx = this.classes.findIndex( (cl) => cl === forClass);
            if (idx >= 0) this.classes.splice(idx, 1);
        },
        formatDate(date: Date) {
          return format(date, "H.mm");
        },
        callPupil(pupil: SendPupil) {
          if (this.selectedClasses.indexOf(pupil.fromClass) >= 0) {
            this.calledPupils.unshift({
              ...pupil,
              atTime: new Date(),
              color: this.colors.nextColor(),
              random: Math.random(),
            });
            if (("Notification" in window) && Notification.permission === "granted") {
              notification(`${pupil.pupil.name} - ${pupil.fromClass}`, {
                body: "Učenec na vratih",
                image: logo,
                renotify: true,
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
        },
        removeCalledPupil(pupil: SendPupil) {
          removePupil(pupil.fromClass, pupil.pupil);
          while(true) {
            const idx = this.calledPupils.findIndex((p) => p.fromClass == pupil.fromClass && p.pupil.name == pupil.pupil.name)
            if (idx >= 0) {
              this.calledPupils.splice(idx, 1);
            } else {
              break;
            }
          }
        },
    }
})
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