<template>
  <div class="overlay" v-if="visible" @click.self="$emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h3>{{ isEdit ? '编辑学生' : '新增学生' }}</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>
      <form @submit.prevent="submit">
        <div class="form-grid">
          <div class="field">
            <label>姓名 <span class="req">*</span></label>
            <input v-model="form.name" required placeholder="学生姓名" />
          </div>
          <div class="field">
            <label>性别 <span class="req">*</span></label>
            <select v-model="form.gender" required>
              <option value="">请选择</option>
              <option value="男">男</option>
              <option value="女">女</option>
            </select>
          </div>
          <div class="field">
            <label>年龄 <span class="req">*</span></label>
            <input v-model.number="form.age" type="number" required placeholder="年龄" min="1" max="150" />
          </div>
          <div class="field">
            <label>学号 <span class="req">*</span></label>
            <input v-model="form.studentNo" required placeholder="唯一学号" />
          </div>
          <div class="field">
            <label>班级</label>
            <input v-model="form.className" placeholder="所在班级" />
          </div>
          <div class="field">
            <label>电话</label>
            <input v-model="form.phone" placeholder="联系电话" />
          </div>
        </div>
        <div class="field full">
          <label>地址</label>
          <input v-model="form.address" placeholder="家庭地址" />
        </div>
        <div class="modal-footer">
          <button type="button" class="cancel" @click="$emit('close')">取消</button>
          <button type="submit">{{ isEdit ? '保存修改' : '确认添加' }}</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  props: { visible: Boolean, student: Object },
  emits: ['close', 'save'],
  data() {
    return {
      form: { name: '', gender: '', age: null, studentNo: '', className: '', phone: '', address: '' }
    }
  },
  computed: {
    isEdit() { return this.student && this.student.id }
  },
  watch: {
    student: {
      immediate: true,
      handler(val) {
        if (val) {
          this.form = { ...val }
        } else {
          this.form = { name: '', gender: '', age: null, studentNo: '', className: '', phone: '', address: '' }
        }
      }
    }
  },
  methods: {
    submit() {
      this.$emit('save', { ...this.form })
    }
  }
}
</script>

<style scoped>
.overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.45);
  display: flex; justify-content: center; align-items: center; z-index: 1000;
  backdrop-filter: blur(2px);
}
.modal {
  background: #fff; border-radius: 16px; width: 560px; max-height: 85vh;
  overflow-y: auto; box-shadow: 0 20px 60px rgba(0,0,0,0.15);
  animation: slideUp 0.25s ease;
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.modal-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 24px 28px 0;
}
.modal-header h3 { font-size: 20px; font-weight: 700; color: #1a1a2e; }
.close-btn {
  width: 32px; height: 32px; border: none; background: #f5f5f5;
  border-radius: 8px; font-size: 18px; cursor: pointer; color: #999;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.15s;
}
.close-btn:hover { background: #ff4d4f; color: #fff; }
form { padding: 24px 28px 28px; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 0 20px; }
.field { margin-bottom: 20px; }
.field.full { grid-column: 1 / -1; }
.field label { display: block; font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; }
.req { color: #ff4d4f; }
.field input, .field select {
  width: 100%; padding: 10px 14px; border: 2px solid #e8e8e8; border-radius: 10px;
  font-size: 14px; outline: none; transition: border-color 0.2s;
  background: #fafafa;
}
.field input:focus, .field select:focus { border-color: #667eea; background: #fff; }
.modal-footer {
  display: flex; gap: 12px; justify-content: flex-end; margin-top: 8px;
  padding-top: 20px; border-top: 1px solid #f0f0f0;
}
.modal-footer button {
  padding: 10px 28px; border: none; border-radius: 10px;
  cursor: pointer; font-size: 14px; font-weight: 600;
  transition: all 0.15s;
}
.modal-footer button[type="submit"] {
  background: linear-gradient(135deg, #667eea, #764ba2); color: #fff;
}
.modal-footer button[type="submit"]:hover {
  transform: translateY(-1px); box-shadow: 0 6px 20px rgba(102,126,234,0.4);
}
.modal-footer .cancel {
  background: #f5f5f5; color: #666;
}
.modal-footer .cancel:hover { background: #e8e8e8; }
</style>
