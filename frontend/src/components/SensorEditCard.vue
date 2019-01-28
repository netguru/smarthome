<template>
    <v-card>
      <v-card-title class="grey" primary-title>{{title}}</v-card-title>
      <v-card-text>
        <v-form >
          <v-container fluid>
            <v-layout wrap row>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.name" label="Sensor name" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.topic" label="Mqtt topic" />
              </v-flex>
              <v-flex xs12 md4>
                <v-text-field v-model="mSensor.transform" label="JSON transform" />
              </v-flex>
              <v-flex xs12 md4>
                <v-select :items="items" v-model="mSensor.returnType" label="Return type"></v-select>
              </v-flex>
            </v-layout>
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" @click="$emit('saveClicked', mSensor)">Save</v-btn>
        <v-btn color="primary" @click="$emit('cancelClicked')" class="hidden-sm-and-up">Cancel</v-btn>
        <v-btn color="red"  dark @click="$emit('deleteClicked')" v-if="deleteButton">Delete</v-btn>
      </v-card-actions>
    </v-card>
</template>
<script>
import clonedeep from 'lodash.clonedeep'
export default {
    name: 'SensorEdit',
    props: {
      title: String,
      deleteButton: Boolean,
      sensor: Object
    },

    watch: {
      sensor(value){
        this.mSensor = clonedeep(value);
      }
    },
    data() {
      return {
        items: ['BOOLEAN', 'INT', 'FLOAT', 'STRING'],
        mSensor: clonedeep(this.sensor),
      }
    },
}
</script>

