<template>
  <nav class="sidebar">
    <div class="logo">
      <div class="logo-icon">S</div>
      <span>学生管理系统</span>
    </div>
    <div class="nav-links">
      <router-link to="/dashboard">
        <span class="icon">&#9632;</span>
        <span>数据概览</span>
      </router-link>
      <router-link to="/students">
        <span class="icon">&#9632;</span>
        <span>学生管理</span>
      </router-link>
    </div>
    <div class="spacer"></div>
    <div class="user-card">
      <div class="avatar">{{ username.charAt(0).toUpperCase() }}</div>
      <div>
        <div class="user-name">{{ username }}</div>
        <div class="user-role">{{ role === 'ADMIN' ? '管理员' : '普通用户' }}</div>
      </div>
    </div>
    <button class="logout-btn" @click="logout">退出登录</button>
  </nav>
</template>

<script>
export default {
  data() {
    return { username: '', role: '' }
  },
  mounted() {
    const user = localStorage.getItem('user')
    if (user) {
      const u = JSON.parse(user)
      this.username = u.username
      this.role = u.role
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.sidebar {
  position: fixed; left: 0; top: 0; bottom: 0;
  width: 220px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  display: flex; flex-direction: column;
  z-index: 100;
}
.logo {
  display: flex; align-items: center; gap: 12px;
  padding: 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.08);
}
.logo-icon {
  width: 36px; height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px; display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 18px;
}
.logo span { font-size: 16px; font-weight: 600; }
.nav-links { padding: 16px 12px; flex: 1; }
.nav-links a {
  display: flex; align-items: center; gap: 12px;
  color: rgba(255,255,255,0.65); text-decoration: none;
  padding: 12px 16px; border-radius: 10px;
  font-size: 14px; font-weight: 500;
  margin-bottom: 4px;
  transition: all 0.2s;
}
.nav-links a:hover { color: #fff; background: rgba(255,255,255,0.08); }
.nav-links a.router-link-active {
  color: #fff; background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}
.nav-links .icon { font-size: 10px; }
.spacer { flex: 1; }
.user-card {
  display: flex; align-items: center; gap: 12px;
  padding: 16px 20px; border-top: 1px solid rgba(255,255,255,0.08);
}
.avatar {
  width: 36px; height: 36px; border-radius: 50%;
  background: linear-gradient(135deg, #f093fb, #f5576c);
  display: flex; align-items: center; justify-content: center;
  font-weight: 600; font-size: 15px;
}
.user-name { font-size: 13px; font-weight: 600; }
.user-role { font-size: 11px; color: rgba(255,255,255,0.5); margin-top: 2px; }
.logout-btn {
  margin: 12px 16px 24px;
  padding: 10px; background: rgba(255,255,255,0.06);
  color: rgba(255,255,255,0.7); border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px; cursor: pointer; font-size: 13px; font-weight: 500;
  transition: all 0.2s;
}
.logout-btn:hover { background: rgba(255,77,79,0.2); color: #ff4d4f; border-color: rgba(255,77,79,0.3); }
</style>
