<template>
  <v-layout row >
    <v-flex grow>{{transform.name}}:</v-flex>
    <v-layout
      md3
      v-if="transform.returnType=='BOOLEAN'"
      class="text-xs-right"
        shrink
        align-end
    >
        <v-img :src="booleanPath" v-if="icon" :height="40" :width="40" contain/>
        <div v-if="!icon">{{transform.event.data}}</div>
    </v-layout>
    <v-layout md4 v-if="transform.returnType=='INT'" class="text-xs-right"
        shrink
        align-end>
        <v-img :src="intPath" v-if="icon" :height="40" :width="40" contain/>
        <div v-if="!icon">{{transform.event.data}}</div>
    </v-layout>
    <v-flex md4 v-if="transform.returnType=='FLOAT'" class="text-xs-right">{{transform.event.data}}</v-flex>
    <v-flex md4 v-if="transform.returnType=='STRING'" class="text-xs-right">{{transform.event.data}}</v-flex>
  </v-layout>
</template>
<script>

export default {
  name: "TransformView",
  props: {
    transform: Object,
    icon: Boolean,
  },
  computed: {
      booleanPath() {
          return require(`@/assets/icons/booleanIcons/${this.transform.icon}-${this.transform.event.data}.png`);
      },
      intPath() {
          let value = Math.floor(this.transform.event.data / 10) * 10;
          return require(`@/assets/icons/intIcons/${this.transform.icon}-${value}.png`);
      }
  },
};
</script>
