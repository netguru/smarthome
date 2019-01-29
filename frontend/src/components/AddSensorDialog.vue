<template>
  <v-dialog v-model="isOpened" :fullscreen="$vuetify.breakpoint.xsOnly">
    <v-btn outline flat icon slot="activator">
      <v-icon>add</v-icon>
    </v-btn>
    <SensorEditCard
      title="Add sensor"
      @cancelClicked='isOpened= false'
      :sensor='{}'
      @refresh='refresh'
      createNew
      />
  </v-dialog>
</template>

<script>
import SensorEditCard from '@/components/SensorEditCard.vue';

export default {
  name: 'AddSensorDialog',
  components: {
    SensorEditCard,
  },
  data() {
    return {
      isOpened: false,
    };
  },
  methods: {
    refresh(){
      this.isOpened = false;
      this.$emit('refresh');
    }
  }
};
</script>
