<template>
  <v-layout row v-on:click.stop="clicked()">
    <v-flex grow>{{transform.name}}:</v-flex>
    <v-layout
      md3
      v-if="transform.returnType=='BOOLEAN' && notUndef(transform.event)"
      class="text-xs-right"
        shrink
        align-end
    >
        <v-img :src="booleanPath" v-if="icon" :height="40" :width="40" contain/>
        <div v-if="!icon">{{transform.event.data}}</div>
        <div v-if="!notUndef(transform.event)">no data</div>
    </v-layout>
    <v-layout md4 v-if="transform.returnType=='INT' && notUndef(transform.event)" class="text-xs-right"
        shrink
        align-end>
        <v-img :src="intPath" v-if="icon" :height="40" :width="40" contain/>
        <div v-if="!icon">{{transform.event.data}}</div>

    </v-layout>
    <v-layout md4 v-if="transform.returnType=='FLOAT' && notUndef(transform.event)" class="text-xs-right">{{transform.event.data}}</v-layout>
    <v-layout md4 v-if="transform.returnType=='STRING' && notUndef(transform.event)" class="text-xs-right">{{transform.event.data}}</v-layout>
    <v-flex  class="text-xs-right" v-if="!notUndef(transform.event)">no data</v-flex>
  </v-layout>
</template>
<script>
import axios from "axios";

export default {
  name: "TransformView",
  props: {
    transform: Object,
    icon: Boolean,
  },
  computed: {
      booleanPath() {
          if(this.notUndef(this.transform.event)){
                return require(`@/assets/icons/booleanIcons/${this.transform.icon}-${this.transform.event.data}.png`);
          } else {
              return "";
          }

      },
      intPath() {
          if(this.notUndef(this.transform.event)){
                let value = Math.floor(this.transform.event.data / 10) * 10;
                return require(`@/assets/icons/intIcons/${this.transform.icon}-${value}.png`);
          } else {
              return "";
          }

      }
  },
  methods: {
      notUndef(item){
          return typeof item !== 'undefined'
      },
      clicked(){
          switch(this.transform.returnType){
              case "BOOLEAN": {
                  console.log("BOOLEAN");
                  let value = this.transform.event.data
                  if( !this.notUndef(value)){
                        value = false;
                  }

                  break;
              }
              case "INT": {
                  console.log("INT");
                  break;
              }
              case "FLOAT": {
                  console.log("FLOAT");
                  break;
              }
              case "STRING": {
                  console.log("STRING");
                  break;
              }
          }
      }
  }
};
</script>
