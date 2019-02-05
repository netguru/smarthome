<template>
  <v-layout row  class="margins" v-bind:class="transform.action" >
    <v-flex xs12 md4>
      <v-text-field
        ref="name"
        :value="transform.name"
        @input="updateSensorName($event)"
        label="Name"
        :disabled="transform.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-text-field
        ref="transform"
        :value="transform.transform"
        @input="updateSensorTransform($event)"
        label="JSON transform"
        :disabled="transform.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-select
        ref="returnType"
        :items="items"
        :value="transform.returnType"
        @input="updateSensorReturnType($event)"
        label="Return type"
        :disabled="transform.action=='REMOVE'"
      ></v-select>
    </v-flex>
    <v-flex md2>
      <v-btn flat icon v-if="transform.action!='REMOVE'">
        <v-icon color="red" @click="removeClicked">remove_circle</v-icon>
      </v-btn>
      <v-btn flat icon v-if="transform.action=='REMOVE' || transform.action=='UPDATE'">
        <v-icon @click="cancelUpdateClicked">close</v-icon>
      </v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
import clonedeep from "lodash.clonedeep";

export default {
  name: "Transform",
  props: {
    transform: Object,
  },
  data() {
    return {
      modified: clonedeep(this.transform),
      items: ["BOOLEAN", "INT", "FLOAT", "STRING"],
    };
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
      this.$emit("update", this.modified);
    },
    removeClicked() {
      this.$emit("removeClicked");
    },
    cancelUpdateClicked() {
      this.$emit("cancelUpdateClicked");
    },
  },
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
    margin: 2px 0px 2px 0px
}
</style>
