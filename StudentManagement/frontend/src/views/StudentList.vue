<template>
  <div class="student-list">
    <div class="page-header">
      <h1>学生管理</h1>
      <button class="add-btn" @click="openAdd">+ 新增学生</button>
    </div>

    <div class="toolbar">
      <div class="search-box">
        <span class="search-icon">&#128269;</span>
        <input v-model="keyword" placeholder="搜索姓名或学号..." @input="search" />
        <span v-if="keyword" class="clear-btn" @click="keyword='';load()">&#10005;</span>
      </div>
      <span class="count">共 {{ students.length }} 人</span>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th><th>姓名</th><th>性别</th><th>年龄</th><th>学号</th><th>班级</th><th>电话</th><th>地址</th><th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in students" :key="s.id">
            <td class="id-cell">{{ s.id }}</td>
            <td class="name-cell">{{ s.name }}</td>
            <td>
              <span class="gender-tag" :class="s.gender === '男' ? 'male' : 'female'">{{ s.gender }}</span>
            </td>
            <td>{{ s.age }}</td>
            <td class="no-cell">{{ s.studentNo }}</td>
            <td>{{ s.className }}</td>
            <td>{{ s.phone }}</td>
            <td class="addr-cell">{{ s.address }}</td>
            <td class="actions">
              <button class="edit" @click="openEdit(s)">编辑</button>
              <button class="del" @click="handleDelete(s.id)">删除</button>
            </td>
          </tr>
          <tr v-if="students.length === 0">
            <td colspan="9" class="empty">
              <div class="empty-icon">&#128196;</div>
              <p>暂无学生数据</p>
              <p class="empty-sub">点击"新增学生"添加第一条记录</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <StudentForm :visible="formVisible" :student="editingStudent" @close="formVisible = false" @save="handleSave" />
  </div>
</template>

<script>
import StudentForm from '../components/StudentForm.vue'
import { getStudents, addStudent, updateStudent, deleteStudent } from '../api/student'

export default {
  components: { StudentForm },
  data() {
    return {
      students: [],
      keyword: '',
      formVisible: false,
      editingStudent: null
    }
  },
  async mounted() { await this.load() },
  methods: {
    async load() {
      try {
        const { data } = await getStudents(this.keyword)
        if (data.code === 200) this.students = data.data
      } catch (e) { console.error(e) }
    },
    search() {
      clearTimeout(this._timer)
      this._timer = setTimeout(() => this.load(), 300)
    },
    openAdd() {
      this.editingStudent = null
      this.formVisible = true
    },
    openEdit(student) {
      this.editingStudent = { ...student }
      this.formVisible = true
    },
    async handleSave(form) {
      try {
        if (form.id) {
          await updateStudent(form.id, form)
        } else {
          await addStudent(form)
        }
        this.formVisible = false
        await this.load()
      } catch (e) { alert('操作失败') }
    },
    async handleDelete(id) {
      if (!confirm('确定删除该学生？')) return
      try {
        await deleteStudent(id)
        await this.load()
      } catch (e) { alert('删除失败') }
    }
  }
}
</script>

<style scoped>
.student-list { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: 700; color: #1a1a2e; }
.add-btn {
  padding: 10px 24px; background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; border: none; border-radius: 10px;
  font-size: 14px; font-weight: 600; cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.add-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(102,126,234,0.4); }
.toolbar {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 20px;
}
.search-box {
  position: relative; width: 300px;
}
.search-icon { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); font-size: 14px; opacity: 0.4; }
.search-box input {
  width: 100%; padding: 10px 36px 10px 36px;
  border: 2px solid #e8e8e8; border-radius: 10px;
  font-size: 14px; outline: none; background: #fff;
  transition: border-color 0.2s;
}
.search-box input:focus { border-color: #667eea; }
.clear-btn {
  position: absolute; right: 12px; top: 50%; transform: translateY(-50%);
  cursor: pointer; color: #ccc; font-size: 14px;
}
.clear-btn:hover { color: #999; }
.count { font-size: 13px; color: #999; }
.table-wrap {
  background: #fff; border-radius: 16px; overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
table { width: 100%; border-collapse: collapse; }
th {
  padding: 14px 16px; text-align: left; font-size: 12px; font-weight: 600;
  color: #999; text-transform: uppercase; letter-spacing: 0.5px;
  background: #fafafa; border-bottom: 1px solid #f0f0f0;
}
td {
  padding: 14px 16px; font-size: 13px; color: #333;
  border-bottom: 1px solid #f5f5f5;
}
tr:last-child td { border-bottom: none; }
tr:hover td { background: #fafbff; }
.id-cell { color: #999; font-weight: 500; }
.name-cell { font-weight: 600; }
.no-cell { font-family: 'SF Mono', 'Consolas', monospace; font-size: 12px; color: #666; }
.addr-cell { max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #999; }
.gender-tag {
  display: inline-block; padding: 2px 10px; border-radius: 20px;
  font-size: 12px; font-weight: 500;
}
.gender-tag.male { background: #e6f7ff; color: #1890ff; }
.gender-tag.female { background: #fff0f6; color: #eb2f96; }
.actions button {
  padding: 5px 14px; border: none; border-radius: 6px;
  cursor: pointer; font-size: 12px; font-weight: 500; margin-right: 6px;
  transition: opacity 0.15s;
}
.actions button:hover { opacity: 0.8; }
.actions .edit { background: #e6f7ff; color: #1890ff; }
.actions .del { background: #fff2f0; color: #ff4d4f; }
.empty { text-align: center; padding: 60px 0; }
.empty-icon { font-size: 48px; margin-bottom: 12px; }
.empty p { color: #999; font-size: 14px; }
.empty-sub { font-size: 12px; color: #ccc; margin-top: 4px; }
</style>
