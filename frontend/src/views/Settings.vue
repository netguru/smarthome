<template>
  <v-layout column fill-height>
    <v-flex>Settings</v-flex>

    <v-layout row wrap>
      Database:
      <v-flex>
        <v-text-field label="url" v-model="settings.dbUrl"></v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field label="user" v-model="settings.dbUser"></v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field label="pass" v-model="settings.dbPass"></v-text-field>
      </v-flex>
    </v-layout>

    <v-layout row wrap>
      Mqtt Broker:
      <v-flex>
        <v-text-field label="url" v-model="settings.mqttUrl"></v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field label="user" v-model="settings.mqttUser"></v-text-field>
      </v-flex>
      <v-flex>
        <v-text-field label="pass" v-model="settings.mqttPass"></v-text-field>
      </v-flex>
    </v-layout>
    <v-layout>
      <v-flex>
        <v-btn @click="saveSettings()">Save</v-btn>
      </v-flex>
    </v-layout>
  </v-layout>
</template>
<script>
import axios from "axios";

export default {
  name: "Settings",
  data() {
    return {
      settings: {
        dbUrl: "",
        dbUser: "",
        dbPass: "",
        mqttUrl: "",
        mqttUser: "",
        mqttPass: "",
      },
    };
  },
  created() {
    this.getSettings();
  },
  methods: {
    saveSettings() {
      axios
        .put(
          `${process.env.VUE_APP_URL}/save_settings`,
          JSON.stringify(this.settings),
          {
            headers: {
              "Content-Type": "application/json",
            },
          },
        )
        .then((response) => {
          console.log("saved. please restart server");
        });
    },
    getSettings() {
      axios.get(`${process.env.VUE_APP_URL}/settings`)
        .then((response) => {
          console.log(response);
          this.$set(this.settings, "dbUrl", response.data.dbUrl);
          this.$set(this.settings, "dbUser", response.data.dbUser);
          this.$set(this.settings, "dbPass", response.data.dbPass);
          this.$set(this.settings, "mqttUrl", response.data.mqttUrl);
          this.$set(this.settings, "mqttUser", response.data.mqttUser);
          this.$set(this.settings, "mqttPass", response.data.mqttPass);
        });
    },
  },
};
</script>
