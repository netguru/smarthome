<template>
  <div class="app">
    <div class="nav">
      <router-link to="/">Home</router-link> |
      <router-link to="/admin">Admin</router-link>
    </div>
    <router-view/>
  </div>
</template>

<style lang="scss" scoped>
.app {
  padding-left: 30px;
  padding-right: 30px;
}
.nav {
  a {
    color: #2c3e50;
    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>
