<template>
  <v-dialog v-model="isOpened" :fullscreen="$vuetify.breakpoint.xsOnly">
    <v-btn outline flat icon slot="activator">
      <v-icon>add</v-icon>
    </v-btn>
    <SensorEditCard 
      title="Add sensor"
      @saveClicked='saveClicked'
      @cancelClicked='isOpened= false'
      :sensor='{}'
      />
  </v-dialog>
</template>

<script>
import axios from 'axios';
import SensorEditCard from '@/components/SensorEditCard.vue';

export default {
  name: 'AddSensorDialog',
  components: {
    SensorEditCard,
  },
  data() {
    return {
      isOpened: false,
      items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
    };
  },
  methods: {
    saveClicked(item) {
      console.log(item);
      axios
        .put(
          `${this.$store.state.host}/add_sensor`,
          JSON.stringify(item),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          console.log(response);
          if (response.status === 201) {
            this.isOpened = false;
            this.$emit('refresh');
          }
        });
    },
  },
};
</script>
