<template>
  <div>
    <div @click="close" class="fixed left-0 right-0 top-0 bottom-0 bg-gray-300 opacity-80 z-20"></div>
    <div class="fixed flex flex-col justify-center left-0 right-0 top-0 bottom-0 z-20">

      <div class="bg-white mx-auto w-[65%] shadow-lg max-w-lg rounded">

        <div class="flex justify-between items-center border-b border-red-50">
          <h3 class="pl-3">{{ props.pupil.pupil.name }}, {{ props.pupil.pupil.fromClass }}</h3>
          <div @click="close" class="p-2">
            <div class="rotate-45 text-2xl cursor-pointer">+</div>
          </div>
        </div>

        <div class="p-3" v-if="props.pupil.departure && props.pupil.departure.entireDay">
          Otrok danes manjka cel dan.
          <span v-if="props.pupil.departure.remark" class="block">
            Opomba: {{props.pupil.departure.remark}}
          </span>
        </div>
        <div v-else>
          <div class="p-3" v-if="props.pupil.departure">
            Otrok je odšel domov ob {{ formatDate(props.pupil.departure.time) }}
          </div>
          <div class="p-3" v-else-if="props.pupil.summon">
            Otrok je bil poklican k vratom ob {{ formatDate(props.pupil.summon.time) }}
          </div>
          <div class="p-3" v-else>
            <span v-if="props.pupil.departurePlan.leavesAlone">
              Otrok gre domov sam ob {{ formatTime(props.pupil.departurePlan.time)}}.
            </span>
            <span v-else>
              Po otroka pridejo predvidoma ob {{ formatTime(props.pupil.departurePlan.time) }}.
            </span>
          </div>

          <p class="text-center space-x-3 mb-3">
            <button @click="sendHome" v-if="showSendHomeButton()" class="bg-blue-600 text-white rounded p-2 w-[150px]">Poslan/a domov</button>
            <button @click="close" class="bg-yellow-600 text-white rounded p-2 w-[150px]">Zapri</button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>

import {date2Time, stripSeconds} from "@/dateAndTime";
import {pupilDeparted, requestPupilLeaveAlone, requestPupilSummonAck} from "@/pupil";
import {DateTime} from "luxon";

const formatDate = date2Time;
const formatTime = stripSeconds;

/** Emits close when dialog wants to be closed */
const emit = defineEmits(['close'])

const props = defineProps<{
  pupil: dto.DailyDeparture
}>()

function close() {
  emit('close');
}


function showSendHomeButton(): boolean {
  console.log("So is departed?", props.pupil, pupilDeparted(props.pupil))
  return !pupilDeparted(props.pupil);
}

async function sendHome() {
  const req = props.pupil.summon ? requestPupilSummonAck(props.pupil.summon.id) : requestPupilLeaveAlone({
    pupilId: props.pupil.pupil.id,
    time: DateTime.now().toISO()
  })
  const reply = await fetch(req)
  if (reply.ok) close();
  else {
    // TODO notify there was an exception
  }
}
</script>