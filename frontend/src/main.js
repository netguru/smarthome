import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import { MdTable, MdCard } from 'vue-material/dist/components';
import 'vue-material/dist/vue-material.min.css';
import 'vue-material/dist/theme/default.css';

Vue.config.productionTip = false;
Vue.use(MdTable);
Vue.use(MdCard);

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app');
