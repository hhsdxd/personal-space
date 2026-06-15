<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总藏书</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card available">
          <div class="stat-value">{{ stats.available }}</div>
          <div class="stat-label">可借阅</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card borrowed">
          <div class="stat-value">{{ stats.borrowed }}</div>
          <div class="stat-label">已借出</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../utils/request'

const stats = reactive({ total: 0, available: 0, borrowed: 0 })

onMounted(async () => {
  const res = await request.get('/books/stats')
  Object.assign(stats, res.data)
})
</script>

<style scoped>
.stat-card { text-align: center; }
.stat-value { font-size: 40px; font-weight: bold; color: #409EFF; }
.available .stat-value { color: #67C23A; }
.borrowed .stat-value { color: #F56C6C; }
.stat-label { font-size: 14px; color: #909399; margin-top: 10px; }
</style>
