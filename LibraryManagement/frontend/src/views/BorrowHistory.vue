<template>
  <div>
    <el-card>
      <el-table :data="records" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="book.title" label="书名" />
        <el-table-column prop="book.author" label="作者" width="150" />
        <el-table-column prop="book.isbn" label="ISBN" width="180" />
        <el-table-column prop="borrowTime" label="借阅时间" width="180">
          <template #default="{ row }">{{ formatTime(row.borrowTime) }}</template>
        </el-table-column>
        <el-table-column prop="returnTime" label="归还时间" width="180">
          <template #default="{ row }">{{ row.returnTime ? formatTime(row.returnTime) : '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'BORROWED' ? 'warning' : 'success'">
              {{ row.status === 'BORROWED' ? '借阅中' : '已归还' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const records = ref([])
const loading = ref(false)

function formatTime(t) {
  return t ? t.replace('T', ' ').substring(0, 19) : ''
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await request.get('/borrow/my')
    records.value = res.data
  } finally {
    loading.value = false
  }
})
</script>
