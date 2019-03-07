<template>
  <v-layout row class="margins" v-bind:class="transform.action">
      <v-flex xs1 md1>
    <v-checkbox
        :value="transform.writable"
        @change="updateSensorWritable()"
        :disabled="transform.action=='REMOVE'"
        ></v-checkbox>
      </v-flex>

    <v-flex xs12 md4>
      <v-text-field
        :value="transform.name"
        @input="updateSensorName($event)"
        label="Name"
        :disabled="transform.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-text-field
        :value="transform.transform"
        @input="updateSensorTransform($event)"
        label="JSON transform"
        :disabled="transform.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-select
        :items="items"
        :value="transform.returnType"
        @input="updateSensorReturnType($event)"
        label="Return type"
        :disabled="transform.action=='REMOVE'"
      ></v-select>
    </v-flex>

    <v-flex xs1 md1>
      <v-img :src="getIcon()" v-if="transform.icon" @click="chooseIcon = !chooseIcon"/>
    </v-flex>

    <v-btn flat fab v-if="!transform.icon" @click="chooseIcon = !chooseIcon">
      <v-icon>web_asset</v-icon>
    </v-btn>
    <v-btn flat icon v-if="transform.action!='REMOVE'">
      <v-icon color="red" @click="removeClicked">delete</v-icon>
    </v-btn>
    <v-btn icon v-if="transform.action=='REMOVE' || transform.action=='UPDATE'">
      <v-icon @click="cancelUpdateClicked">close</v-icon>
    </v-btn>
    <v-dialog v-model="chooseIcon">
      <v-card>
        <v-card-title>Choose icon</v-card-title>
        <v-card-text>
          <v-layout row wrap v-if="transform.returnType==='BOOLEAN'">
            <v-img
              contain
              width="40"
              height="40"
              v-for="icon in booleanIcons"
              :key="icon"
              :src="getBooleanIcon(icon)"
              @click="selectIcon(icon)"
            />
          </v-layout>

          <v-layout row wrap v-else-if="transform.returnType==='INT'">
            <v-img
              contain
              width="40"
              height="40"
              v-for="icon in intIcons"
              :key="icon"
              :src="getIntIcon(icon)"
              @click="selectIcon(icon)"
            />
          </v-layout>
          <v-layout v-else>Choose Boolean or Int Return Type first.</v-layout>
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-layout>
</template>
<script>
import clonedeep from "lodash.clonedeep";

export default {
  name: "Transform",
  props: {
    transform: Object
  },
  data() {
    return {
      modified: clonedeep(this.transform),
      items: ["BOOLEAN", "INT", "FLOAT", "STRING"],
      chooseIcon: false,
      booleanIcons: [
        "contact",
        "door",
        "fire",
        "frontdoor",
        "garagedoor",
        "heating",
        "light",
        "lock",
        "network",
        "poweroutlet",
        "presence",
        "receiver",
        "screen",
        "siren",
        "switch",
        "wallswitch",
        "washingmachine",
        "window"
      ],

      intIcons: [
        "battery",
        "blinds",
        "cinemascreen",
        "cistern",
        "garagedoor",
        "heating",
        "humidity",
        "light",
        "qualityofservice",
        "rollershutter",
        "sewerage"
      ]
    };
  },
  mounted() {
    this.modified = clonedeep(this.transform)
  },
  updated(){
      this.modified = clonedeep(this.transform)
  },
  methods: {
    updateSensorName(name) {
      this.modified.name = name;
      this.$emit("update", this.modified);
    },
    updateSensorTransform(trans) {
      this.modified.transform = trans;
      this.$emit("update", this.modified);
    },
    updateSensorReturnType(returnType) {
      this.modified.returnType = returnType;
      this.modified.icon = "";
      this.$emit("update", this.modified);
    },
    updateSensorWritable(writable) {
        this.modified.writable = !this.modified.writable
        this.$emit("update", this.modified)
    },
    removeClicked() {
      this.$emit("removeClicked");
    },
    cancelUpdateClicked() {
      this.$emit("cancelUpdateClicked");
    },
    getBooleanIcon(name) {
      return require(`@/assets/icons/booleanIcons/${name}-true.png`);
    },
    getIntIcon(name) {
      return require(`@/assets/icons/intIcons/${name}-70.png`);
    },
    getIcon() {
      switch (this.transform.returnType) {
        case "BOOLEAN":
          return this.getBooleanIcon(this.transform.icon);
          break;
        case "INT":
          return this.getIntIcon(this.transform.icon);
          break;
      }
    },
    selectIcon(icon) {
      this.modified.icon = icon;
      this.$emit("update", this.modified);
      this.chooseIcon = false;
    }
  }
};
</script>

<style lang="scss" scoped>
.REMOVE {
  background: rgba(180, 0, 0, 0.2);
}
.ADD {
  background: rgba(1, 180, 1, 0.2);
}
.UPDATE {
  background: rgba(255, 166, 0, 0.2);
}
.margins {
  margin: 2px 0px 2px 0px;
}
</style>
