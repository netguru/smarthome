<template>
  <v-layout>
    <div class="loading" v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>


        <v-layout row fill-height>
          <v-flex xs12 md4 v-if="content!==null">
            <v-card>
              <v-card-title class="dark" @click="deselect()">
                <div>Sensors</div>
                <v-spacer></v-spacer>
                <AddSensorDialog @refresh="fetchData()"/>
              </v-card-title>
              <v-list>
                <v-list-tile v-for="item in content" :key="item.id" @click="onSelect(item)">
                  <v-list-tile-content>
                    <v-list-tile-title>#{{item.id}} {{item.name}} ({{item.topic}})</v-list-tile-title>
                  </v-list-tile-content>
                </v-list-tile>
              </v-list>
            </v-card>
          </v-flex>
          <v-flex md8 grow class="hidden-sm-and-down ml-3">
            <SensorEditCard
              v-if="editSensor"
              title="Edit Sensor"
              :sensor="editSensor"
              deleteButton
            />

            <v-card
              v-if="editSensor===null"
              height="100%"
              class="justify-center"
            >click + to add sensor or select sensor to edit</v-card>
          </v-flex>
        </v-layout>
  </v-layout>
</template>

<script >
import axios from "axios";
import AddSensorDialog from "@/components/AddSensorDialog.vue";
import SensorEditCard from "@/components/SensorEditCard.vue";

export default {
  name: "Sensors",
  components: {
    AddSensorDialog,
    SensorEditCard
  },
  data() {
    return {
      addSensor: false,
      editSensor: null,
      loading: false,
      content: null,
      error: null
    };
  },
  created() {
    this.fetchData();
  },
  watch: {
    $route: "fetchData"
  },
  methods: {
    fetchData() {
      this.error = null;
      this.loading = true;
      axios
        .get(`${this.$store.state.host}//get_sensors_all`)
        .then(response => {
          this.loading = false;
          this.content = response.data;
        })
        .catch(err => {
          this.loading = false;
          this.error = err.toString();
        });
    },
    onSelect(item) {
      this.editSensor = item;
    },
    deselect() {
      this.editSensor = null;
    }
  }
};
</script>

<style lang="scss" scoped>
.content {
 min-height: 100vh;
 align-items: flex-start;
}
</style>
