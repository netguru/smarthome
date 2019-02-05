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
            <v-dialog v-model="addSensor" :fullscreen="$vuetify.breakpoint.xsOnly">
              <v-btn outline flat icon slot="activator">
                <v-icon>add</v-icon>
              </v-btn>
              <SensorEditCard
                title="Add sensor"
                @cancelClicked="addSensor= false"
                :sensor="{}"
                @refresh="fetchData()"
                createNew
              />
            </v-dialog>
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
          @refresh="fetchData()"
        />

        <v-flex v-if="editSensor===null" fill-height>click + to add sensor or select sensor to edit</v-flex>
      </v-flex>
    </v-layout>
    <v-dialog v-model="editSensorDialogShow" :fullscreen="$vuetify.breakpoint.xsOnly">
      <SensorEditCard
        v-if="editSensor"
        title="Edit sensor"
        @cancelClicked="deselect"
        :sensor="editSensor"
        @refresh="fetchData"
      />
    </v-dialog>
  </v-layout>
</template>

<script >
import axios from "axios";
import SensorEditCard from "@/components/SensorEditCard.vue";

export default {
  name: "Sensors",
  components: {
    SensorEditCard,
  },
  data() {
    return {
      addSensor: false,
      editSensor: null,
      loading: false,
      content: null,
      error: null,
      editSensorDialogShow: false,
    };
  },
  created() {
    this.fetchData();
  },
  watch: {
    $route: "fetchData",
  },
  methods: {
    fetchData() {
      this.deselect();
      this.error = null;
      this.loading = true;
      axios
        .get(`${this.$store.state.host}/get_sensors_all`)
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
      if (window.innerWidth <= 960) {
        this.editSensorDialogShow = true;
      }
    },
    deselect() {
      this.addSensor = false;
      this.editSensor = null;
      this.editSensorDialogShow = false;
    },
  },
};
</script>

<style lang="scss" scoped>
</style>
