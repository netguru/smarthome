<template>
  <v-dialog v-model="isOpened" :fullscreen="$vuetify.breakpoint.xsOnly">
    <v-btn absolute dark fab bottom right color="dark" slot="activator">
      <v-icon>add</v-icon>
    </v-btn>
    <v-card>
      <v-card-title class="grey lighten-2" primary-title>Add sensor</v-card-title>
      <v-card-text>
        <v-form >
          <v-container>
            <v-layout>
              <v-flex xs12 md4>
                <v-text-field v-model="sensor.name" label="Sensor name" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="sensor.topic" label="Mqtt topic" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="sensor.transform" label="JSON transform" />
              </v-flex>
              <v-flex xs12 md4>
                <v-select :items="items" v-model="sensor.returnType" label="Return type"></v-select>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" @click="saveClicked">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from 'axios';

export default {
  name: 'AddSensorDialog',
  data() {
    return {
      isOpened: false,
      sensor: {},
      items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
    };
  },
  methods: {
    saveClicked() {
      axios
        .put(
          `${this.$store.state.host}/add_sensor`,
          JSON.stringify(this.sensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          console.log(response);
          if (response.status === 201) {
            this.isOpened = false;
            this.$emit('refresh');
          }
        });
    },
  },
};
</script>
