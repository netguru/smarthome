<template>
  <div class="outerWrapper" @click.self="$emit('closeModal')">
    <div class="innerWrapper">
        <md-card >
            <slot></slot>
      </md-card>
    </div>
  </div>
</template>
<script>

export default {
  name: 'Modal',
};
</script>
<style lang="scss" scoped>
.outerWrapper {
    width: 100%;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    z-index: 10;
    background: rgba(0, 0, 0, 0.301);
    display: flex;
    flex-direction: column;
    align-content: center;
    justify-content: center;
}
.innerWrapper {
    background: white;
    margin: 50px;
}
.md-card {
    padding: 10px;
}
</style>
