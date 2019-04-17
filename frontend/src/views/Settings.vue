<template>
  <v-layout column align-content-top>
    <v-flex>Settings</v-flex>

    <v-flex>
      Database:
      <v-input messages="url"></v-input>
      <v-input messages="user"></v-input>
      <v-input messages="pass"></v-input>
    </v-flex>

    <v-flex>
      Mqtt Broker
      <v-input messages="url"></v-input>
      <v-input messages="user"></v-input>
      <v-input messages="pass"></v-input>
    </v-flex>
  </v-layout>
</template>
