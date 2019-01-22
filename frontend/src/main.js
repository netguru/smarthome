import Vuetify from 'vuetify';
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';


Vue.config.productionTip = false;
Vue.use(Vuetify, {
  theme: {
    primary: '#80D8FF',
    light: '#b5ffff',
    dark: '#49a7cc',
  },
});

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app');
