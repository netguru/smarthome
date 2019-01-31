<template>
  <v-container fluid grid-list-sm fill-height>
    <v-layout class="loading" v-if="loading">Loading...</v-layout>
    <v-layout v-if="error" class="error">{{ error }}</v-layout>
    <v-layout v-if="loading === false" class="content">
      <v-layout row wrap>
        <v-flex md4 v-for="(sensor, index) in sensors" v-bind:key="sensor.id">
          <v-card>
            <v-card-title>#{{sensor.id}} / {{sensor.name}}</v-card-title>
            <v-card-text>
              Current status: {{getStatusForSensor(index)}}
            </v-card-text>
          </v-card>
        </v-flex>
      </v-layout>
    </v-layout>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Dashboard',
  data() {
    return {
      sensors: null,
      events: null,
      error: null,
      loading: false,
      timer: '',
    };
  },
  created() {
    this.timer = setInterval(this.fetchData, 1000 * 15);
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
          let index;
          const promises = [];
          this.sensors = response.data;
          for (index = 0; index < response.data.length; index += 1) {
            const sensor = response.data[index];
            promises.push(
              axios.get(
                `${this.$store.state.host}/get_events_for_sensor/${
                  sensor.id
                }/1`,
              ),
            );
          }
          Promise.all(promises)
            .then((responses) => {
              this.loading = false;
              this.events = responses.map(it => it.data);
              console.log(this.events);
            })
            .catch((err) => {
              this.loading = false;
              this.error = err.toString();
            });
        })
        .catch((err) => {
          this.loading = false;
          this.error = err.toString();
        });
    },
    getStatusForSensor(index){
      let status = this.events[index][0]
      if(status){
        console.log(status)
        return status.data
      } else {
        return "No data"
      }
    }
  },
};
</script>
<style lang="scss" scoped>
</style>
