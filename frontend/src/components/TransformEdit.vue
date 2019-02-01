<template>
  <v-layout row v-bind:class="value.action">
    
    <v-flex xs12 md4>
      <v-text-field 
      ref="name" 
      :value="value.name" 
      @input="updateSensor()" 
      label="Name"
      :disabled="value.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-text-field
        ref="transform"
        :value="value.transform"
        @input="updateSensor()"
        label="JSON transform"
        :disabled="value.action=='REMOVE'"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-select
        ref="returnType"
        :items="items"
        :value="value.returnType"
        @input="updateSensor()"
        label="Return type"
        :disabled="value.action=='REMOVE'"
      ></v-select>
    </v-flex>
    <v-flex>
      <v-btn flat icon v-if="value.action!='REMOVE'">
        <v-icon color="red" @click="removeClicked">remove_circle</v-icon>
      </v-btn>
      <v-btn flat icon v-if="value.action=='REMOVE' || value.action=='UPDATE'">
        <v-icon @click="cancelRemoveClicked">close</v-icon>
      </v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
export default {
  name: "Transform",
  props: {
    value: Object,
  },
  data() {
    return {
      items: ["BOOLEAN", "INT", "FLOAT", "STRING"],
      action: "none",
    };
  },
  methods: {
    updateSensor() {
      this.$emit("input", {
        action: "UPDATE",
        id: +this.value.id,
        sensor_id: +this.value.sensor_id,
        name: +this.$refs.name.value,
        transform: +this.$refs.transform.value,
        returnType: +this.$refs.returnType.value,
      });
    },
    removeClicked() {
      this.$emit("removeClicked");
    },
    cancelRemoveClicked() {
      this.$emit("cancelRemoveClicked");
    }
  },
};
</script>

<style lang="scss" scoped>
.REMOVE {
  background: rgba(180, 0, 0, 0.5);
}
.ADD {
  background:rgba(1, 180, 1, 0.5);
}
.UPDATE {
  background: rgba(255, 166, 0, 0.5);
}
</style>

