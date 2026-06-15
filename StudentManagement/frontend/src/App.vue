<template>
  <div class="app">
    <Sidebar v-if="isLoggedIn" />
    <div class="main" :class="{ 'has-sidebar': isLoggedIn }">
      <router-view />
    </div>
    <AiChat v-if="isLoggedIn" context="student" />
  </div>
</template>

<script>
import Sidebar from './components/Sidebar.vue'
import AiChat from './components/AiChat.vue'

export default {
  name: 'App',
  components: { Sidebar, AiChat },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('token')
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

* { margin: 0; padding: 0; box-sizing: border-box; }
body {
  font-family: 'Inter', 'Microsoft YaHei', 'PingFang SC', sans-serif;
  background: #f0f2f5;
  -webkit-font-smoothing: antialiased;
}
.app { display: flex; min-height: 100vh; }
.main { flex: 1; }
.main.has-sidebar { margin-left: 220px; }
</style>
