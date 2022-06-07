<template>
    <div v-if="selectedClass">
      <ul class="selectPupil">
        <li v-for="pupil in pupils">
          <a href="#" @click.prevent="sendPupil(pupil)">{{ pupil.name }}</a>
        </li>
      </ul>
      <button @click="cancel">Prekini</button>
    </div>
    <div v-else>
        <h4>Iz katerega razreda je učenec?</h4>
        <ul class="selectClass">
            <li v-for="cls in classes">
                <a href="#" @click.prevent="selectClass(cls)">{{ cls }}</a>
            </li>
        </ul>
    </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import fakestate, { type Data, type Pupil } from "../data";
import {sendMessage} from '../main';
import type { SendPupil } from '../events';

export default defineComponent({
    data() {
        return {
            numeral: '',
            selectedClass: '',
        }
    },
    computed: {
        currentPin() {
            let stars = '';
            for (let a = 0; a < this.numeral.length; a++) {
                stars += '*';
            }
            return stars;
        },
        classes() {
            return Array.from(fakestate.classes.keys());
        },
        pupils(): Pupil[] {
            return fakestate.classes.get(this.selectedClass)!.pupils;
        }
    },
    methods: {
        press(numeral: string, event: Event) {
            (event.target as HTMLElement).classList.add("blink");
            this.numeral += numeral;
        },
        cancel() {
            this.selectedClass = '';
        },
        selectClass(cls: string) {
            this.selectedClass = cls;
            this.numeral = '';
        },
        animationEnded(event: Event) {
            (event.target as HTMLElement).classList.remove("blink");
        },
        sendPupil(pupil: Pupil) {
          const payload: SendPupil = {
            pupil, fromClass: this.selectedClass
          };
          sendMessage(JSON.stringify(payload))
          this.cancel()
        }
    }
})
</script>


<style lang="scss" scoped>

ul.selectClass {
  list-style-type: none;
  font-size: 23px;
  li {
    margin: 0.25em 0.25em;
  }
}

.selectPupil {
  list-style-type: none;
  li {
    margin: 0.25em 0.25em;
  }
}


.keypad {
    display: flex;
    flex-direction: column;
    width: 600px;
    justify-content: center;


    .row {
        display: flex;
        flex-direction: row;

        .key {
            text-align: center;
            width: 60px;
            height: 60px;

            display: flex;
            align-items: center;
            justify-content: center;
            animation-play-state: paused;

            &.blink {
                animation: blink 0.15s ease-in-out 1;
                animation-play-state: running;
            }
        }
    }
}

@keyframes blink{
  0% {
    background: white;
  }
  50% {
    background: red;
  }
  100% {
    background: white;
  }
}
</style>