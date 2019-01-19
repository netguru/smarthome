<template>
  <div class="admin">


    <div class="loading" v-if="loading">Loading...</div>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="content" class="content">
      <md-table v-model="content" md-card @md-selected="onSelect">
        <md-table-toolbar>
          <h1 class="md-title">Configured sensors</h1>
        </md-table-toolbar>

        <md-table-row slot="md-table-row" slot-scope="{ item }" md-selectable="single">
          <md-table-cell md-label="ID" md-numeric>{{ item.id }}</md-table-cell>
          <md-table-cell md-label="Name">{{ item.name }}</md-table-cell>
          <md-table-cell md-label="Topic">{{ item.topic }}</md-table-cell>
          <md-table-cell md-label="Transform">{{ item.transform }}</md-table-cell>
          <md-table-cell md-label="Return type">{{ item.returnType }}</md-table-cell>
        </md-table-row>
      </md-table>

      <md-button class="md-fab md-primary" @click="addSensor = true">
        <md-icon>add</md-icon>
      </md-button>
    </div>

    <EditSensorModal v-if="selected" :item="selected" @closeModal="editSensorClosed"/>
    <AddSensorModal v-if="addSensor" @closeModal="addSensorClosed"/>
  </div>
</template>

<script >
import axios from 'axios';
import EditSensorModal from '@/components/EditSensorModal.vue';
import AddSensorModal from '@/components/AddSensorModal.vue';

export default {
  name: 'Admin',
  data() {
    return {
      selected: null,
      addSensor: false,
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
  components: {
    EditSensorModal,
    AddSensorModal,
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
    },
  },
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
.new{
  padding: 20px;
}
.md-fab {
   position: fixed;
  bottom: 15px;
  right: 15px;
}
</style>
