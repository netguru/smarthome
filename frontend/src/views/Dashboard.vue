<template>
  <v-layout>
    <div class="loading" v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="loading === false" class="content">
      <ul>
        <p>Sensors:</p>
        <li v-for="(sensor, index) in sensors" v-bind:key="sensor.id">
          <p>#{{sensor.id}} / {{sensor.name}}</p>
          <ul>
            <p>Events:</p>
            <li v-for="event in events[index]" v-bind:key="event.id">
              {{event.id}},
                 {{event.timeStamp}}, {{event.data}}
            </li>
          </ul>
        </li>
      </ul>
    </div>
  </v-layout>
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
      console.log('refresh');
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
                }/10`,
              ),
            );
          }
          Promise.all(promises)
            .then((responses) => {
              this.loading = false;
              this.events = responses.map(it => it.data);
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
  },
};
</script>
<style lang="scss" scoped>
</style>
