<template>
  <el-dialog
    :title="isEdit ? '编辑图书' : '新增图书'"
    v-model="visible"
    width="500px"
    @close="resetForm"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="书名" prop="title">
        <el-input v-model="form.title" placeholder="请输入书名" />
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author" placeholder="请输入作者" />
      </el-form-item>
      <el-form-item label="ISBN" prop="isbn">
        <el-input v-model="form.isbn" placeholder="请输入ISBN" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const emit = defineEmits(['success'])
const visible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({ title: '', author: '', isbn: '' })
const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }]
}

function open(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    form.title = row.title
    form.author = row.author
    form.isbn = row.isbn
  } else {
    isEdit.value = false
    editId.value = null
  }
  visible.value = true
}

function resetForm() {
  form.title = ''
  form.author = ''
  form.isbn = ''
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/books/${editId.value}`, form)
      ElMessage.success('更新成功')
    } else {
      await request.post('/books', form)
      ElMessage.success('添加成功')
    }
    visible.value = false
    emit('success')
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
