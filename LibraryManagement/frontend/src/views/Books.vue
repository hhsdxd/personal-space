<template>
  <div>
    <el-card>
      <div style="display:flex;justify-content:space-between;margin-bottom:15px">
        <el-input v-model="keyword" placeholder="搜索书名/作者" style="width:300px" clearable @clear="fetchBooks" @keyup.enter="fetchBooks">
          <template #append>
            <el-button @click="fetchBooks" icon="Search" />
          </template>
        </el-input>
        <el-button type="primary" @click="openDialog(null)" v-if="authStore.isAdmin()">新增图书</el-button>
      </div>
      <el-table :data="books" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="author" label="作者" width="150" />
        <el-table-column prop="isbn" label="ISBN" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.available ? 'success' : 'danger'">{{ row.available ? '可借' : '已借出' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleBorrow(row)" :disabled="!row.available">借阅</el-button>
            <el-button type="success" size="small" @click="handleReturn(row)" :disabled="row.available">归还</el-button>
            <template v-if="authStore.isAdmin()">
              <el-button type="warning" size="small" @click="openDialog(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <BookDialog ref="dialogRef" @success="fetchBooks" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import request from '../utils/request'
import BookDialog from '../components/BookDialog.vue'

const authStore = useAuthStore()
const books = ref([])
const keyword = ref('')
const loading = ref(false)
const dialogRef = ref(null)

async function fetchBooks() {
  loading.value = true
  try {
    const params = keyword.value ? { keyword: keyword.value } : {}
    const res = await request.get('/books', { params })
    books.value = res.data
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  dialogRef.value.open(row)
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除《${row.title}》？`, '提示', { type: 'warning' })
  await request.delete(`/books/${row.id}`)
  ElMessage.success('删除成功')
  fetchBooks()
}

async function handleBorrow(row) {
  await request.post(`/borrow/${row.id}`)
  ElMessage.success(`借阅《${row.title}》成功`)
  fetchBooks()
}

async function handleReturn(row) {
  await request.post(`/borrow/return/${row.id}`)
  ElMessage.success(`归还《${row.title}》成功`)
  fetchBooks()
}

onMounted(fetchBooks)
</script>
