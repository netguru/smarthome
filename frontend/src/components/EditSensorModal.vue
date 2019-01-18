<template>
  <Modal @closeModal="$emit('closeModal',false)">
    <p>Edit sensor #{{edit.id}}</p>

    <EditFields v-model="edit"/>

    <div>
      <md-button>Save changes</md-button>
      <md-button @click="removeSensor">Remove sensor</md-button>
    </div>
  </Modal>
</template>
<script>
import axios from 'axios';
import Modal from '@/components/Modal.vue';
import EditFields from '@/components/EditFields.vue';

export default {
  name: 'EditSensorModal',
  components: {
    Modal,
    EditFields,
  },
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  methods: {
    removeSensor() {
      axios
        .delete(`${this.$store.state.host}/remove_sensor/${this.item.id}`)
        .then((response) => {
          if (response.status === 200) {
            this.$emit('closeModal', true);
          }
        });
    },
  },
  data() {
    return {
      edit: this.item,
    };
  },
};
</script>
