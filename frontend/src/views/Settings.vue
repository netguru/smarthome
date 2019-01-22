<template>
  <div class="admin">
    <div class="loading" v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <v-container fluid>
      <v-layout>
        <v-flex xs4>
          <v-card >
              <v-card-title>Sensors</v-card-title>
              
              <AddSensorDialog @refresh="fetchData()"/>
            <v-list>
              <v-list-tile v-for="item in content" :key="item.id" @click="onSelect(item)">
                <v-list-tile-content>
                  <v-list-tile-title>#{{item.id}} {{item.name}} ({{item.topic}})</v-list-tile-title>
                </v-list-tile-content>
              </v-list-tile>
            </v-list>
          </v-card>
        </v-flex >
        <v-flex class="hidden-sm-and-down">
          <v-card>
            dupaaaa
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script >
import axios from 'axios';
import AddSensorDialog from '@/components/AddSensorDialog.vue';
import EditSensorDialog from '@/components/EditSensorDialog.vue';

export default {
  name: 'Settings',
  components: {
    AddSensorDialog,
    EditSensorDialog,
  },
  data() {
    return {
      addSensor: false,
      editSensor: {
        open: false,
        sensor: {
          id: '',
          name: '',
          topic: '',
          transform: '',
          returnType: 'BOOLEAN',
        },
      },
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
      this.editSensor.open = true;
      this.editSensor.sensor = item;
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
