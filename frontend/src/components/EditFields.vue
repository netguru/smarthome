<template>
  <div class="edit_fields">
    <md-field>
      <label>Name</label>
      <md-input v-model="data.name" ></md-input>
    </md-field>
    <md-field>
      <label>Topic</label>
      <md-input v-model="data.topic" ></md-input>
    </md-field>
    <md-field>
      <label>Transform</label>
      <md-input v-model="data.transform" ></md-input>
    </md-field>
    <md-field>
      <label for="returnType">Return value</label>
      <md-select v-model="data.returnType" name="returnType" id="returnType">
        <md-option value="BOOLEAN">Boolean</md-option>
        <md-option value="INT">Int</md-option>
        <md-option value="FLOAT">Float</md-option>
        <md-option value="STRING">String</md-option>
      </md-select>
    </md-field>
  </div>
</template>
<script>
export default {
  name: 'EditFields',
  props: {
    value: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      data: this.value,
    };
  },
};
</script>
<style lang="scss" scoped>
.md-field {
  margin-left: 15px;
  width: 300px;
}
.edit_fields {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}
</style>
