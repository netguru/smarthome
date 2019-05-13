<template>
  <v-layout class="flexcard">
    <v-progress-circular indeterminate color="primary" v-if="inProgress"></v-progress-circular>
    <v-card v-if="error">{{error}}</v-card>

    <v-card height="100%" v-if="!inProgress && !error">
      <v-card-title class="dark" primary-title>{{title}}</v-card-title>

      <v-card-text class="grow">
        <v-form>
          <v-container fluid grid-list-sm>
            <v-layout column>
              <v-layout row>
                <v-flex xs12 md12>
                  <v-text-field v-model="mSensor.name" label="Sensor name"/>
                </v-flex>
              </v-layout>

              <v-layout row class="dark">
                <v-flex>Characteristic:</v-flex>
                <v-spacer></v-spacer>
                <v-btn outline flat icon @click="addTransform()">
                  <v-icon>add</v-icon>
                </v-btn>
              </v-layout>

              <v-layout row>
                <v-flex md3>
                  <v-list>
                    <v-list-tile
                      v-for="transform in mSensor.transforms"
                      :key="transform.id"
                      @click="onSelect(transform)"
                    >
                      <v-list-tile-content>
                        <v-list-tile-title>#{{transform.id}} {{transform.name}}</v-list-tile-title>
                      </v-list-tile-content>
                    </v-list-tile>
                  </v-list>
                </v-flex>
                <v-flex>
                  <!-- CHARACTERISTIC EDIT -->
                  <v-layout
                    v-if="selected!==null"
                    column
                    class="margins"
                    v-bind:class="selected.action"
                  >
                    <v-flex>
                      <v-text-field
                        v-model="selected.name"
                        label="Name"
                        :disabled="selected.action=='REMOVE'"
                        @input="characteristicChanged()"
                      />
                    </v-flex>
                    <v-flex>
                      <v-text-field
                        v-model="selected.topic"
                        label="Topic"
                        :disabled="selected.action=='REMOVE'"
                        @input="characteristicChanged()"
                      />
                    </v-flex>
                    <v-flex>
                      <v-text-field
                        v-model="selected.transform"
                        label="JSON transform"
                        :disabled="selected.action=='REMOVE'"
                        @input="characteristicChanged()"
                      />
                    </v-flex>
                    <v-flex>
                      <v-select
                        :items="items"
                        v-model="selected.returnType"
                        label="Return type"
                        :disabled="selected.action=='REMOVE'"
                        @input="characteristicChanged()"
                      ></v-select>
                    </v-flex>

                    <v-flex v-if="selected.returnType === 'BOOLEAN'">
                        <v-layout row>
                            <v-text-field
                                v-model="selected.boolOn"
                                label="On state label"
                                :disabled="selected.action=='REMOVE'"
                                @input="characteristicChanged()"
                            />
                            <v-text-field
                                v-model="selected.boolOff"
                                label="Off state label"
                                :disabled="selected.action=='REMOVE'"
                                @input="characteristicChanged()"
                            />
                        </v-layout>
                    </v-flex>
                    <v-flex>
                        <v-checkbox
                          v-model="selected.writable"
                          :disabled="selected.action=='REMOVE'"
                          @change="writableClicked()"
                          label="Writable"
                        ></v-checkbox>
                    </v-flex>

                    <v-flex v-if="selected.writable">
                        <v-text-field
                                v-model="selected.cmdTopic"
                                label="Write command topic"
                                :disabled="selected.action=='REMOVE'"
                                @input="characteristicChanged()"
                            />
                    </v-flex>
                    <v-layout row>

                      <v-flex v-if="selected.icon">
                        Icon:
                        <v-img
                          :src="getIcon()"
                          @click="chooseIcon = !chooseIcon"
                          aspect-ratio="1"
                          width="70"
                        />
                      </v-flex>

                      <v-btn flat fab v-if="!selected.icon" @click="chooseIcon = !chooseIcon">
                        Icon: <v-icon>web_asset</v-icon>
                      </v-btn>
                      <v-btn flat icon v-if="selected.action!='REMOVE'">
                        <v-icon color="red" @click="removeTransformClicked">delete</v-icon>
                      </v-btn>
                      <v-btn icon v-if="selected.action=='REMOVE' || selected.action=='UPDATE'">
                        <v-icon @click="cancelUpdateClicked">close</v-icon>
                      </v-btn>
                    </v-layout>

                    <v-dialog v-model="chooseIcon">
                      <v-card>
                        <v-card-title>Choose icon</v-card-title>
                        <v-card-text>
                          <v-layout row wrap v-if="selected.returnType==='BOOLEAN'">
                            <v-img
                              contain
                              width="40"
                              height="40"
                              v-for="icon in booleanIcons"
                              :key="icon"
                              :src="getBooleanIcon(icon)"
                              @click="selectIcon(icon)"
                            />
                          </v-layout>

                          <v-layout row wrap v-else-if="selected.returnType==='INT'">
                            <v-img
                              contain
                              width="40"
                              height="40"
                              v-for="icon in intIcons"
                              :key="icon"
                              :src="getIntIcon(icon)"
                              @click="selectIcon(icon)"
                            />
                          </v-layout>
                          <v-layout v-else>Choose Boolean or Int Return Type first.</v-layout>
                        </v-card-text>
                      </v-card>
                    </v-dialog>
                  </v-layout>
                  <!-- CHARACTERISTIC EDIT -->
                </v-flex>
              </v-layout>
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

export default {
  name: "SensorEdit",
  props: {
    title: String,
    deleteButton: Boolean,
    sensor: Object,
    createNew: Boolean,
  },

  watch: {
    sensor(value) {
      this.mSensor = clonedeep(value);
      this.selected = null;
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
      selected: null,
      selectedBackup: null,
      items: ["BOOLEAN", "INT", "FLOAT", "STRING"],
      chooseIcon: false,
      booleanIcons: [
        "contact",
        "door",
        "fire",
        "frontdoor",
        "garagedoor",
        "heating",
        "light",
        "lock",
        "network",
        "poweroutlet",
        "presence",
        "receiver",
        "screen",
        "siren",
        "switch",
        "wallswitch",
        "washingmachine",
        "window",
      ],

      intIcons: [
        "battery",
        "blinds",
        "cinemascreen",
        "cistern",
        "garagedoor",
        "heating",
        "humidity",
        "light",
        "qualityofservice",
        "rollershutter",
        "sewerage",
      ],
    };
  },
  mounted() {
    if (!this.mSensor.transforms) {
      this.$set(this.mSensor, "transforms", []);
    }
    for (const transform of this.mSensor.transforms) {
      this.$set(transform, "action", "");
    }
  },
  methods: {
    onSelect(transform) {
      this.selected = transform;
      this.selectedBackup = clonedeep(transform);
    },
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
          `${process.env.VUE_APP_URL}/add_sensor`,
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
          `${process.env.VUE_APP_URL}/modify_sensor/${this.mSensor.id}`,
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
          this.error = `${error.response.status} - ${
            error.response.statusText
          }`;
        });
    },
    removeClicked() {
      this.inProgress = true;
      axios
        .delete(`${process.env.VUE_APP_URL}/remove_sensor/${this.mSensor.id}`)
        .then((response) => {
          this.inProgress = false;
          if (response.status === 200) {
            this.$emit("refresh");
          }
        });
    },
    // transfoorm methods:
    removeTransformClicked() {
      console.log("remove transform clicked");
      if (this.selected.action === "ADD") {
        this.mSensor.transforms.splice(index, 1);
      } else {
        this.selected.action = "REMOVE";
      }
    },
    cancelUpdateClicked() {
      const transform = this.selected;
      console.log("cancelUpdateClicked clicked");
      this.selected.action = "";
      this.selected = clonedeep(this.selectedBackup);
    },
    addTransform() {
      this.mSensor.transforms.push({ action: "ADD" });
    },
    characteristicChanged() {
      if (this.selected.action != "ADD") {
        this.selected.action = "UPDATE";
      }
    },
    writableClicked(){
        if(this.selected.writable === false){
            this.selected.cmdTopic = null;
        }
        this.characteristicChanged();
    },
    getBooleanIcon(name) {
      return `icons/booleanIcons/${name}-true.png`;
    },
    getIntIcon(name) {
      return `icons/intIcons/${name}-70.png`;
    },
    getIcon() {
      switch (this.selected.returnType) {
        case "BOOLEAN":
          return this.getBooleanIcon(this.selected.icon);
          break;
        case "INT":
          return this.getIntIcon(this.selected.icon);
          break;
      }
    },
    selectIcon(icon) {
      this.selected.icon = icon;
      this.chooseIcon = false;
      this.characteristicChanged();
    },
  },
};
</script>
<style lang="scss" scoped>
.flexcard {
  display: flex;
  flex-direction: column;
}
.REMOVE {
  background: rgba(180, 0, 0, 0.2);
}
.ADD {
  background: rgba(1, 180, 1, 0.2);
}
.UPDATE {
  background: rgba(255, 166, 0, 0.2);
}
</style>
