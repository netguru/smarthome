import Vuetify from 'vuetify';
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';


Vue.config.productionTip = false;
Vue.use(Vuetify, {
  theme: {
    primary: '#80D8FF',
    accent: '#C6FF00',
  },
});

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app');
