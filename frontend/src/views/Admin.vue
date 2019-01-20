<template>
  <div class="admin">
    <div class="loading" v-if="loading">Loading...</div>
    <div v-if="error" class="error">{{ error }}</div>

    <v-card>
    <v-toolbar color="indigo" dark>
      <v-toolbar-title>Sensors</v-toolbar-title>
       <v-btn
              absolute
              dark
              fab
              bottom
              right
              color="pink"
            >
              <v-icon>add</v-icon>
            </v-btn>
    </v-toolbar>
    <v-list>
      <v-list-tile v-for="item in content" :key="item.id" @click="onSelect(item)">
        <v-list-tile-content>
          <v-list-tile-title>#{{item.id}} {{item.name}} ({{item.topic}})</v-list-tile-title>
        </v-list-tile-content>
      </v-list-tile>
    </v-list>
    </v-card>

   
    <EditSensorModal v-if="selected" :item="selected" @closeModal="editSensorClosed"/>
    <AddSensorModal v-if="addSensor" @closeModal="addSensorClosed"/>
  </div>
</template>

<script >
import axios from "axios";
import EditSensorModal from "@/components/EditSensorModal.vue";
import AddSensorModal from "@/components/AddSensorModal.vue";

export default {
  name: "Admin",
  data() {
    return {
      selected: null,
      addSensor: false,
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
  components: {
    EditSensorModal,
    AddSensorModal
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
      this.selected = item;
      this.edit = item;
    },
    addSensorClosed(refresh) {
      this.addSensor = false;
      if (refresh) {
        this.fetchData();
      }
    },
    editSensorClosed(refresh) {
      this.selected = null;
      if (refresh) {
        this.fetchData();
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.admin {
  font-size: 15pt;
}
.md-card {
  margin-top: 15px;
  margin-bottom: 15px;
}
.selected {
  padding: 20px;
}
.new {
  padding: 20px;
}
.md-fab {
  position: fixed;
  bottom: 15px;
  right: 15px;
}
</style>
