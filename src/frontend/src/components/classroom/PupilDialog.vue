<template>
  <MobileFriendlyDialog @close="close">
    <template v-slot:heading>
      {{ props.pupil.pupil.name }}, {{ props.pupil.pupil.fromClass }}
    </template>

    <div class="p-3" v-if="pupilAbsentToday">
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
    </div>
    <div>
      <Activities :pupil-id="props.pupil.pupil.id" class="p-3" />
      <div v-if="!pupilAbsentToday">
        <p class="text-center space-y-3 md:space-x-3 mb-3">
          <button @click="sendHome" v-if="showSendHomeButton()" class="bg-my-blue text-white rounded p-2 w-[150px]">Poslan/a domov</button>
          <button @click="cancelDeparture" v-if="showCancelDepartureButton()" class="bg-my-blue text-white rounded p-2 w-[150px]">Prekliči odhod</button>
          <button @click="close" class="bg-sandy text-white rounded p-2 w-[150px]">Zapri</button>
        </p>
      </div>
    </div>

  </MobileFriendlyDialog>
</template>
<script lang="ts" setup>

import {date2Time, stripSeconds} from "@/dateAndTime";
import {cancelTodaysDepartures, pupilDeparted, requestPupilLeaveAlone, requestPupilSummonAck} from "@/pupil";
import {DateTime} from "luxon";
import MobileFriendlyDialog from "../MobileFriendlyDialog.vue";
import Activities from "./PupilActivities.vue"
import {computed} from "vue";

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

const pupilAbsentToday = computed(() => props.pupil.departure && props.pupil.departure.entireDay)

function showSendHomeButton(): boolean {
  return !pupilDeparted(props.pupil);
}

function showCancelDepartureButton(): boolean {
  return pupilDeparted(props.pupil);
}

async function sendHome() {
  const req = props.pupil.summon ? requestPupilSummonAck(props.pupil.summon.id) : requestPupilLeaveAlone({
    pupilId: props.pupil.pupil.id,
    time: (DateTime.now().toISO() as any) as dto.OffsetDateTime,
  })
  const reply = await fetch(req)
  if (reply.ok) close();
  else {
    // TODO notify there was an exception
  }
}

async function cancelDeparture() {
  const req = cancelTodaysDepartures({
    pupilId: props.pupil.pupil.id,
    time: (DateTime.now().toISO() as any) as dto.OffsetDateTime,
  })
  const reply = await fetch(req)
  if (reply.ok) {
    props.pupil.departure = null; //optimization so the UI updates at once
    close();
  }
  else {
    // TODO notify there was an exception
  }
}
</script>