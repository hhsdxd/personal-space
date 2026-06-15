<template>
  <div class="auth-page">
    <div class="auth-right">
      <div class="auth-card">
        <h2>创建账号</h2>
        <p class="sub">注册后即可使用完整功能</p>
        <form @submit.prevent="handleRegister">
          <div class="field">
            <label>用户名</label>
            <input v-model="form.username" placeholder="3-20位字符" required autocomplete="off" />
          </div>
          <div class="field">
            <label>密码</label>
            <input v-model="form.password" type="password" placeholder="6-30位字符" required />
          </div>
          <div class="field">
            <label>确认密码</label>
            <input v-model="confirmPwd" type="password" placeholder="再次输入密码" required />
          </div>
          <p class="error" v-if="error">{{ error }}</p>
          <button type="submit">注 册</button>
        </form>
        <p class="tip">已有账号？<router-link to="/login">返回登录</router-link></p>
      </div>
    </div>
    <div class="auth-left">
      <div class="brand">
        <div class="brand-icon">S</div>
        <h1>学生管理系统</h1>
        <p>第一个注册的用户自动成为管理员</p>
      </div>
    </div>
  </div>
</template>

<script>
import { register } from '../api/auth'

export default {
  data() {
    return { form: { username: '', password: '' }, confirmPwd: '', error: '' }
  },
  methods: {
    async handleRegister() {
      try {
        this.error = ''
        if (this.form.password !== this.confirmPwd) {
          this.error = '两次输入的密码不一致'
          return
        }
        const { data } = await register(this.form)
        if (data.code === 200) {
          localStorage.setItem('token', data.data.token)
          localStorage.setItem('user', JSON.stringify(data.data))
          this.$router.push('/dashboard')
        } else {
          this.error = data.message
        }
      } catch (e) {
        this.error = '请求失败，请检查网络连接'
      }
    }
  }
}
</script>

<style scoped>
.auth-page { display: flex; min-height: 100vh; }
.auth-left {
  flex: 1; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex; align-items: center; justify-content: center;
}
.brand { text-align: center; color: #fff; }
.brand-icon {
  width: 80px; height: 80px; background: rgba(255,255,255,0.2);
  border-radius: 24px; display: inline-flex; align-items: center; justify-content: center;
  font-size: 36px; font-weight: 700; margin-bottom: 24px;
  backdrop-filter: blur(10px);
}
.brand h1 { font-size: 32px; font-weight: 700; margin-bottom: 8px; }
.brand p { font-size: 16px; opacity: 0.8; }
.auth-right {
  flex: 1; display: flex; align-items: center; justify-content: center; background: #fff;
}
.auth-card { width: 400px; }
.auth-card h2 { font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.auth-card .sub { color: #999; font-size: 14px; margin-bottom: 32px; }
.field { margin-bottom: 20px; }
.field label { display: block; font-size: 13px; font-weight: 600; color: #333; margin-bottom: 6px; }
.field input {
  width: 100%; padding: 12px 16px; border: 2px solid #e8e8e8; border-radius: 10px;
  font-size: 15px; transition: border-color 0.2s; outline: none;
  background: #fafafa;
}
.field input:focus { border-color: #667eea; background: #fff; }
button {
  width: 100%; padding: 14px; background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; border: none; border-radius: 10px;
  font-size: 16px; font-weight: 600; cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s; margin-top: 4px;
}
button:hover { transform: translateY(-1px); box-shadow: 0 8px 25px rgba(102,126,234,0.4); }
.error {
  color: #ff4d4f; font-size: 13px; margin-bottom: 12px;
  background: #fff2f0; padding: 8px 12px; border-radius: 6px;
}
.tip { text-align: center; margin-top: 24px; font-size: 14px; color: #999; }
.tip a { color: #667eea; font-weight: 600; text-decoration: none; }
.tip a:hover { text-decoration: underline; }
</style>
