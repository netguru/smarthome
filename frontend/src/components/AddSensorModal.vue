<template>
  <Modal @closeModal="$emit('closeModal',false)">
    <p>Add sensor</p>
    <EditFields v-model="sensor"/>

    <div>
      <md-button @click="addSensor">Add sensor</md-button>
    </div>
  </Modal>
</template>
<script>
import axios from 'axios';
import Modal from '@/components/Modal.vue';
import EditFields from '@/components/EditFields.vue';

export default {
  name: 'AddSensorModal',
  components: {
    Modal,
    EditFields,
  },
  data() {
    return {
      sensor: {
        name: '',
        topic: '',
        transform: '',
        returnType: 'BOOLEAN',
      },
    };
  },
  methods: {
    addSensor() {
      axios
        .put(
          `${this.$store.state.host}/add_sensor`,
          JSON.stringify(this.sensor),
          {
            headers: {
              'Content-Type': 'application/json',
            },
          },
        )
        .then((response) => {
            console.log(response);
          if (response.status == 201) {
            console.log('emitting closemodal');
            this.$emit('closeModal', true);
          }
        });
    },
  },
};
</script>
<style lang="scss" scoped>
</style>
