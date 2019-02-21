<template>
  <v-layout class="flexcard">
    <v-progress-circular indeterminate color="primary" v-if="inProgress"></v-progress-circular>
    <v-card v-if="error">{{error}}</v-card>

    <v-card height="100%"  v-if="!inProgress && !error">
      <v-card-title class="dark" primary-title>{{title}}</v-card-title>

      <v-card-text class="grow">
        <v-form>
          <v-container fluid grid-list-sm>
            <v-layout column>
              <v-layout row>
                <v-flex xs12 md6>
                  <v-text-field v-model="mSensor.name" label="Sensor name"/>
                </v-flex>
                <v-flex xs12 md6>
                  <v-text-field v-model="mSensor.topic" label="Mqtt topic"/>
                </v-flex>
              </v-layout>

              <v-layout row class="dark">
                <v-flex>Transforms:</v-flex>
                <v-spacer></v-spacer>
                <v-btn outline flat icon @click="addTransform()">
                  <v-icon>add</v-icon>
                </v-btn>
              </v-layout>

              <Transform
                v-for="(transform, index) in mSensor.transforms"
                :key="transform.id"
                :transform="mSensor.transforms[index]"
                @removeClicked="removeTransformClicked(index)"
                @cancelUpdateClicked="cancelUpdateClicked(index)"
                @update="updateTransform(index,$event)"
              />
            </v-layout>
          </v-container>
        </v-form>
      </v-card-text>

      <v-card-actions>
        <v-btn color="primary" @click="saveClicked">Save</v-btn>
        <v-btn color="primary" @click="$emit('cancelClicked')" class="hidden-sm-and-up">Cancel</v-btn>
        <v-btn color="red" dark @click="removeClicked" v-if="deleteButton">Delete sensor</v-btn>
      </v-card-actions>
    </v-card>
  </v-layout>
</template>
<script>
import clonedeep from "lodash.clonedeep";
import axios from "axios";
import Transform from "@/components/TransformEdit.vue";

export default {
  name: "SensorEdit",
  components: {
    Transform,
  },
  props: {
    title: String,
    deleteButton: Boolean,
    sensor: Object,
    createNew: Boolean,
  },

  watch: {
    sensor(value) {
      this.mSensor = clonedeep(value);
      if (!this.mSensor.transforms) {
        this.$set(this.mSensor, "transforms", []);
      }
    },
  },
  data() {
    return {
      mSensor: clonedeep(this.sensor),
      inProgress: false,
      error: null,
    };
  },
  mounted() {
    if (!this.mSensor.transforms) {
      this.$set(this.mSensor, "transforms", []);
    }
  },
  methods: {
    saveClicked() {
      this.inProgress = true;
      if (this.createNew) {
        this.create();
      } else {
        this.edit();
      }
    },
    create() {
      axios
        .put(
          `${this.$store.state.host}/add_sensor`,
          JSON.stringify(this.mSensor),
          {
            headers: {
              "Content-Type": "application/json",
            },
          },
        )
        .then((response) => {
          this.inProgress = false;
          if (response.status === 201) {
            this.$emit("refresh");
          }
        });
    },
    edit() {
      axios
        .patch(
          `${this.$store.state.host}/modify_sensor/${this.mSensor.id}`,
          JSON.stringify(this.mSensor),
          {
            headers: {
              "Content-Type": "application/json",
            },
          },
        )
        .then((response) => {
          this.inProgress = false;
          if (response.status === 200) {
            this.$emit("refresh");
          }
        })
        .catch((error) => {
          this.inProgress = false;
          this.error = `${error.response.status} - ${error.response.statusText}`;
        });
    },
    removeClicked() {
      this.inProgress = true;
      axios
        .delete(`${this.$store.state.host}/remove_sensor/${this.mSensor.id}`)
        .then((response) => {
          this.inProgress = false;
          if (response.status === 200) {
            this.$emit("refresh");
          }
        });
    },
    // transfoorm methods:
    removeTransformClicked(index) {
      if (this.mSensor.transforms[index].action === "ADD") {
        this.mSensor.transforms.splice(index, 1);
      } else {
        this.$set(this.mSensor.transforms[index], "action", "REMOVE");
      }
    },
    cancelUpdateClicked(index) {
      const transform = this.mSensor.transforms[index];
      if (transform.action === "REMOVE") {
        this.$set(transform, "action", "");
      } else if (transform.action === "UPDATE") {
        this.$set(transform, "action", "");
      }
      this.$set(transform, "name", this.sensor.transforms[index].name);
      this.$set(
        transform,
        "transform",
        this.sensor.transforms[index].transform,
      );
      this.$set(
        transform,
        "returnType",
        this.sensor.transforms[index].returnType,
      );
      this.$set(
        transform,
        "icon",
        this.sensor.transforms[index].icon,
      );
    },
    addTransform() {
      this.mSensor.transforms.push({ action: "ADD" });
    },
    updateTransform(index, event) {
      this.$set(this.mSensor.transforms[index], "name", event.name);
      this.$set(this.mSensor.transforms[index], "transform", event.transform);
      this.$set(this.mSensor.transforms[index], "returnType", event.returnType);
      this.$set(this.mSensor.transforms[index], "icon", event.icon);
      this.$set(this.mSensor.transforms[index], "writable", event.writable)
      if (this.mSensor.transforms[index].action !== "ADD") {
        this.mSensor.transforms[index].action = "UPDATE";
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.flexcard {
  display: flex;
  flex-direction: column;
}
</style>
