<template>
  <nav>
    <v-toolbar flat app class="primary">
      <v-toolbar-side-icon @click="drawer = !drawer"/>
      <v-toolbar-title>Smarthome</v-toolbar-title>
    </v-toolbar>

    <v-navigation-drawer app v-model="drawer" dark>
      <v-list>
        <v-list-tile v-for="link in links" :key="link.text" router :to="link.route">
          <v-list-tile-action>
            <v-icon class="white--text">{{ link.icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title class="white--text">{{ link.text }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
  </nav>
</template>

<script>
export default {
  data() {
    return {
      drawer: false,
      links: [
        { icon: "dashboard", text: "Dashboard", route: "/" },
        { icon: "settings", text: "Sensors", route: "/sensors" },
        { icon: "group_work", text: "Groups", route: "/groups" },
        { icon: "description", text: "Rules", route: "/rules" },
      ],
    };
  },
};
</script>
