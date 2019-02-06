<template>
  <v-app>
    <Navbar/>

    <v-content>
      <v-container fluid fill-height grid-list-sm>
        <router-view></router-view>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import Navbar from "./components/Navbar.vue";

export default {
  components: { Navbar },
  name: "App",
  data() {
    return {};
  },
};
</script>
