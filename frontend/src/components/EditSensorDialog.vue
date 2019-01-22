<template>
  <v-dialog v-model="show">
    <v-card>
      <v-card-title class="grey lighten-2" primary-title>
          Edit sensor ({{value.sensor.id}})</v-card-title>
      <v-card-text>
        <v-form>
          <v-container>
            <v-layout>
              <v-flex xs12 md4>
                <v-text-field v-model="value.sensor.name" label="Sensor name"/>
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="value.sensor.topic" label="Mqtt topic"/>
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="value.sensor.transform" label="JSON transform"/>
              </v-flex>
              <v-flex xs12 md4>
                <v-select :items="items" v-model="value.sensor.returnType" label="Return type"/>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" @click="saveClicked">Edit</v-btn>
        <v-btn color="red" @click="removeClicked">Remove Sensor</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EditSensorDialog',
  props: {
    value: Object,
  },
  data() {
    return {
      items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
    };
  },
  computed: {
    show: {
      get() {
        return this.value.open;
      },
      set(value) {
        this.$emit('input', { ...this.value, open: value });
      },
    },
  },
  methods: {
    saveClicked() {
      axios
        .patch(
          `${this.$store.state.host}/modify_sensor/${this.value.sensor.id}`,
          JSON.stringify(this.value.sensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          if (response.status === 200) {
            this.show = false;
            this.$emit('refresh');
          }
        });
    },
    removeClicked() {
      axios
        .delete(`${this.$store.state.host}/remove_sensor/${this.value.sensor.id}`)
        .then((response) => {
          if (response.status === 200) {
            this.show = false;
            this.$emit('refresh');
          }
        });
    },
  },
};
</script>
