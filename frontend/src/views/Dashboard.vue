<template>
  <v-layout align-content-start>
    <v-layout v-if="loading">Loading...</v-layout>
    <v-layout v-if="error">{{ error }}</v-layout>
    <v-layout v-if="loading === false && error === null">
      <v-layout row wrap >
        <v-flex xs12 md3 v-for="sensor in sensors" v-bind:key="sensor.id">
          <v-card >
            <v-card-title class="dark">{{sensor.name}}</v-card-title>
            <v-card-text>
              <v-layout column>
                <v-flex v-for="transform in sensor.transforms" :key="transform.id">
                  <TransformView
                    :transform="transform"
                    :icon="typeof transform.icon  !== 'undefined'"
                    v-on:refresh="fetchData()"
                    v-on:showDialog="showDialog($event)"
                  />
                </v-flex>
              </v-layout>
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-layout>
    <v-dialog v-model="intDialogShow">
      <v-card>
          <v-card-title class="dark" primary-title>Set value</v-card-title>
          <v-card-text>
              <v-slider
                label="SLIDER"
                value="30"
                v-on:end="intSliderEnd()"
                ></v-slider>
          </v-card-text>
      </v-card>
    </v-dialog>
  </v-layout>
</template>

<script>
import axios from "axios";
import TransformView from "@/components/TransformView.vue";

export default {
  name: "Dashboard",
  components: { TransformView },
  data() {
    return {
      sensors: null,
      error: null,
      loading: false,
      timer: "",
      intDialogShow: false,
      dialogTransform: null,
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
        .then(response => {
          this.sensors = response.data;
          this.loading = false;
          this.sensors.forEach(sensor => {
            sensor.transforms.forEach(transform => {
              axios
                .get(
                  `${this.$store.state.host}/get_events_for_transform/${
                    transform.id
                  }/1`
                )
                .then(responseTrans => {
                  this.$set(transform, "event", responseTrans.data[0]);
                });
            });
          });
        })
        .catch(err => {
          this.loading = false;
          this.error = err.toString();
        });
    },
    showDialog(dialogType){
        this.dialogTransform = dialogType;
        this.intDialogShow = true;
    },
    intSliderEnd(){

    }
  }
};
</script>
