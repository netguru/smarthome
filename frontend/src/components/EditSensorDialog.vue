<template>
  <v-dialog v-model="show">
    
  </v-dialog>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EditSensorDialog',
  props: {
    value: Object,
  },
  data() {
    return {
      items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
    };
  },
  computed: {
    show: {
      get() {
        return this.value.open;
      },
      set(value) {
        this.$emit('input', { ...this.value, open: value });
      },
    },
  },
  methods: {
    saveClicked() {
      axios
        .patch(
          `${this.$store.state.host}/modify_sensor/${this.value.sensor.id}`,
          JSON.stringify(this.value.sensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
          if (response.status === 200) {
            this.show = false;
            this.$emit('refresh');
          }
        });
    },
    removeClicked() {
      axios
        .delete(`${this.$store.state.host}/remove_sensor/${this.value.sensor.id}`)
        .then((response) => {
          if (response.status === 200) {
            this.show = false;
            this.$emit('refresh');
          }
        });
    },
  },
};
</script>
