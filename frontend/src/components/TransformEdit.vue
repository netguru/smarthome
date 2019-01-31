<template>
  <v-layout row>
    <v-flex xs12 md4>
      <v-text-field ref="name" :value="value.name" @input="updateSensor()" label="Name"/>
    </v-flex>
    <v-flex xs12 md4>
      <v-text-field
        ref="transform"
        :value="value.transform"
        @input="updateSensor()"
        label="JSON transform"
      />
    </v-flex>
    <v-flex xs12 md4>
      <v-select
        ref="returnType"
        :items="items"
        :value="value.returnType"
        @input="updateSensor()"
        label="Return type"
      ></v-select>
    </v-flex>
    <v-flex>
      <v-btn flat icon>
        <v-icon color="red" @click="removeClicked">remove_circle</v-icon>
      </v-btn>
    </v-flex>
  </v-layout>
</template>
<script>
export default {
  name: "Transform",
  props: {
    value: Object
  },
  data() {
    return {
      items: ["BOOLEAN", "INT", "FLOAT", "STRING"]
    };
  },
  methods: {
    updateSensor(item) {
      this.$emit("input", {
        id: this.value.id,
        sensor_id: this.value.sensor_id,
        name: +this.$refs.name.value,
        transform: +this.$refs.transform.value,
        returnType: +this.$refs.returnType.value
      });
    },
    removeClicked(){
        this.$emit("removeClicked");
    }
  }
};
</script>

