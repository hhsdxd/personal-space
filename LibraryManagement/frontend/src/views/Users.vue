<template>
  <div>
    <el-card>
      <el-table :data="users" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">{{ row.role === 'ADMIN' ? '管理员' : '用户' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const users = ref([])
const loading = ref(false)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 19) : ''
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await request.get('/users')
    users.value = res.data
  } finally {
    loading.value = false
  }
})
</script>
