<template>
    <v-card height="100%" class="flexcard">
      <v-card-title class="grey" primary-title>{{title}}</v-card-title>
      <v-card-text class="grow">
        <v-form >
          <v-container fluid>
            <v-layout wrap row>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.name" label="Sensor name" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.topic" label="Mqtt topic" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.transform" label="JSON transform" />
              </v-flex>
              <v-flex xs12 md4>
                <v-select :items="items" v-model="mSensor.returnType" label="Return type"></v-select>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" @click="saveClicked">Save</v-btn>
        <v-btn color="primary" @click="$emit('cancelClicked')" class="hidden-sm-and-up">Cancel</v-btn>
        <v-btn color="red"  dark @click="removeClicked" v-if="deleteButton">Delete</v-btn>
      </v-card-actions>
    </v-card>
</template>
<script>
import clonedeep from 'lodash.clonedeep';
import axios from 'axios';

export default {
  name: 'SensorEdit',
  props: {
    title: String,
    deleteButton: Boolean,
    sensor: Object,
    createNew: Boolean,
  },

  watch: {
    sensor(value) {
      this.mSensor = clonedeep(value);
    },
  },
  data() {
    return {
      items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
      mSensor: clonedeep(this.sensor),
    };
  },
  methods: {
    saveClicked() {
      if(this.createNew){
        this.create();
      } else {
        this.edit();
      }
    },
    create() {
      axios
        .put(
          `${this.$store.state.host}/add_sensor`,
          JSON.stringify(this.mSensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          if (response.status === 201) {
            this.$emit('refresh');
          }
        });
    },
    edit() {
      axios
        .patch(
          `${this.$store.state.host}/modify_sensor/${this.mSensor.id}`,
          JSON.stringify(this.mSensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          if (response.status === 200) {
            this.$emit('refresh');
          }
        });
    },
    removeClicked() {
      console.log("delete clicked")
      axios
        .delete(`${this.$store.state.host}/remove_sensor/${this.mSensor.id}`)
        .then((response) => {
          if (response.status === 200) {
            this.$emit('refresh');
          }
        });
    },
  },
};
</script>
<style lang="scss" scoped>
.flexcard {
  display: flex;
  flex-direction: column;
}
</style>
