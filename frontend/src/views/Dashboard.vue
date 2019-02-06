<template>
  <v-container fluid grid-list-sm fill-height>
    <v-layout v-if="loading">Loading...</v-layout>
    <v-layout v-if="error">{{ error }}</v-layout>
    <v-layout v-if="loading === false && error === null">
      <v-layout row wrap>
        <v-flex md2 v-for="sensor in sensors" v-bind:key="sensor.id">
          <v-card>
            <v-card-title class="dark">{{sensor.name}}</v-card-title>
            <v-card-text>
              <v-layout column>
                  <v-flex v-for="transform in sensor.transforms" :key="transform.id">
                     <v-layout row >
                        <v-flex>{{transform.name}}:</v-flex>
                     <v-spacer></v-spacer>
                      <v-flex v-if="transform.event">
                        {{transform.event.data}}
                      </v-flex>
                     </v-layout>
                  </v-flex>
              </v-layout>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-layout>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "Dashboard",
  data() {
    return {
      sensors: null,
      error: null,
      loading: false,
      timer: "",
    };
  },
  created() {
    this.timer = setInterval(this.fetchData, 1000 * 30);
    this.fetchData();
  },
  destroyed() {
    clearInterval(this.timer);
  },
  methods: {
    fetchData() {
      this.loading = true;
      axios
        .get(`${this.$store.state.host}/get_sensors_all`)
        .then((response) => {
          this.sensors = response.data;
          this.loading = false;
          this.sensors.forEach((sensor) => {
            sensor.transforms.forEach((transform) => {
              axios.get(`${this.$store.state.host}/get_events_for_transform/${transform.id}/1`)
                .then((responseTrans) => {
                  this.$set(transform, "event", responseTrans.data[0]);
                });
            });
          });
        })
        .catch((err) => {
          this.loading = false;
          this.error = err.toString();
        });
    },
  },
};
</script>
