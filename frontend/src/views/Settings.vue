<template>
  <div >
    <div class="loading" v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <v-container fluid fill-height>
      <v-layout row wrap fill-height>
        
        <v-flex xs12 md4 v-if="content!==null">
          <v-card>
            <v-card-title class="dark" @click="deselect()">
              <div>
                Sensors
                </div>
              <v-spacer></v-spacer>
              <AddSensorDialog @refresh='fetchData()' />
            </v-card-title>
            <v-list>
              <v-list-tile v-for="item in content" :key="item.id" @click="onSelect(item)">
                <v-list-tile-content>
                  <v-list-tile-title>#{{item.id}} {{item.name}} ({{item.topic}})</v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
            </v-list>
          </v-card>

        </v-flex >

        <v-flex md8 grow class="pl-2 hidden-sm-and-down">
          <SensorEditCard v-if="editSensor"
            title="Edit Sensor"
            :sensor='editSensor'
            deleteButton
          />

          <div v-if="editSensor===null">
            click + to add sensor or select sensor to edit
          </div>
        </v-flex>

      </v-layout>
    </v-container>


  </div>
</template>

<script >
import axios from 'axios';
import AddSensorDialog from '@/components/AddSensorDialog.vue';
import SensorEditCard from '@/components/SensorEditCard.vue';

export default {
  name: 'Settings',
  components: {
    AddSensorDialog,
    SensorEditCard,
  },
  data() {
    return {
      addSensor: false,
      editSensor: null,
      loading: false,
      content: null,
      error: null,
    };
  },
  created() {
    this.fetchData();
  },
  watch: {
    $route: 'fetchData',
  },
  methods: {
    fetchData() {
      this.error = null;
      this.loading = true;
      axios
        .get(`${this.$store.state.host}//get_sensors_all`)
        .then((response) => {
          this.loading = false;
          this.content = response.data;
        })
        .catch((err) => {
          this.loading = false;
          this.error = err.toString();
        });
    },
    onSelect(item) {
       this.editSensor = item;
    },
    deselect() {
      this.editSensor = null;
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
