import Vuetify from "vuetify";
import Vue from "vue";
import App from "./App.vue";
import router from "./router";

Vue.config.productionTip = false;
Vue.use(Vuetify, {
  theme: {
    primary: "#80D8FF",
    light: "#b5ffff",
    dark: "#AEE5FF",
  },
});

new Vue({
  router,
  render: h => h(App),
}).$mount("#app");
