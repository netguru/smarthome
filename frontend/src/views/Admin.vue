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
        <md-table-cell md-label="ID"  md-numeric>{{ item.id }}</md-table-cell>
        <md-table-cell md-label="Name" >{{ item.name }}</md-table-cell>
        <md-table-cell md-label="Topic" >{{ item.topic }}</md-table-cell>
        <md-table-cell md-label="Transform" >{{ item.transform }}</md-table-cell>
        <md-table-cell md-label="Return type" >{{ item.returnType }}</md-table-cell>
      </md-table-row>
    </md-table>

    <p>Selected:</p>
    {{ selected }}
    </div>
  </div>
</template>

<script >
import axios from 'axios';

const host = 'http://localhost:8080';
export default {
  name: "Admin",
  data() {
    return {
        selected: {},
      loading: false,
      content: null,
      error: null,
    };
  },
  created() {
    // fetch the data when the view is created and the data is
    // already being observed
    this.fetchData();
  },
  watch: {
    // call again the method if the route changes
    $route: 'fetchData',
  },
  methods: {
    fetchData() {
      this.error = null;
      this.loading = true;
      axios.get(`${host}/get_sensors_all`)
        .then((response) => {
          this.loading = false;
          this.content = response.data;
        })
        .catch((err) => {
          this.loading = false;
          this.error = err.toString();
        });
    },
     onSelect (item) {
        this.selected = item;
      },
  },
};
</script>

<style lang="scss" scoped>

.admin {
  font-size: 15pt;
}
.md-card {
    margin: 30px;
}
</style>
