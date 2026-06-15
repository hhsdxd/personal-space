<template>
  <el-container style="min-height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div class="logo">图书管理系统</div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/books">
          <el-icon><Reading /></el-icon>
          <span>图书管理</span>
        </el-menu-item>
        <el-menu-item index="/borrow">
          <el-icon><Timer /></el-icon>
          <span>借阅记录</span>
        </el-menu-item>
        <el-menu-item index="/users" v-if="authStore.isAdmin()">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;display:flex;justify-content:flex-end;align-items:center;border-bottom:1px solid #e6e6e6">
        <span style="margin-right:15px">{{ authStore.username }}（{{ authStore.isAdmin() ? '管理员' : '用户' }}）</span>
        <el-button type="danger" text @click="handleLogout">退出</el-button>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
  <AiChat context="library" />
</template>

<script setup>
import AiChat from './AiChat.vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  color: #fff; text-align: center; padding: 20px 0; font-size: 18px;
  font-weight: bold; border-bottom: 1px solid #4a5d72;
}
.el-menu { border-right: none; }
</style>
