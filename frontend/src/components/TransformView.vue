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
  data() {
    return {
      intDialogShow: false,
    };
  },
  computed: {
    booleanPath() {
      if (this.notUndef(this.transform.event)) {
        return `icons/booleanIcons/${this.transform.icon}-${this.transform.event.data}.png`;
      }
      return "";
    },
    intPath() {
      if (this.notUndef(this.transform.event)) {
        const value = Math.floor(this.transform.event.data / 10) * 10;
        return `icons/intIcons/${this.transform.icon}-${value}.png`;
      }
      return "";
    },
  },
  methods: {
    notUndef(item) {
      return typeof item !== "undefined";
    },
    clicked() {
      if(this.transform.writable){
        switch (this.transform.returnType) {
            case "BOOLEAN": {
            console.log("BOOLEAN");
            let value = this.transform.event.data;
            if (this.notUndef(value)) {
                if (value.toLowerCase() === "true") {
                value = "false";
                } else {
                value = "true";
                }
            } else {
                value = "false";
            }
            const field = this.transform.transform.split(".").slice(-1)[0];
            const event = {
                sensorId: this.transform.sensorId,
                transformId: this.transform.id,
                data: value,
            };
            axios.put(`${process.env.VUE_APP_URL}/add_event`, JSON.stringify(event),
                {
                headers: {
                    "Content-Type": "application/json",
                },
                })
                .then((response) => {
                    setTimeout(() => {
                        this.$emit("refresh");
                    }, 1500);
                });
            break;
            }
            case "INT": {
            console.log("INT");
            this.$emit("showDialog", this.transform);
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
    },
  },
};
</script>
