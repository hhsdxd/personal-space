# 🚀 Personal Space — 全栈项目展示平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Live Demo](https://img.shields.io/badge/Live-Demo-667eea)](http://8.138.135.98)

> 一站式全栈项目展示平台，集成**图书管理系统**、**学生管理系统**和 **AI 智能助手**。  
> 基于 Spring Boot + Vue 3 前后端分离架构，接入**通义千问大模型**，Docker 一键部署。

## 🎯 在线体验

👉 **http://8.138.135.98**

默认管理员账号：`admin` / `admin123`

## 🏗 技术架构

```
┌──────────────────────────────────────────────┐
│                   Nginx (80)                  │
│              反向代理 + 静态资源               │
└────┬──────────┬──────────┬───────────────────┘
     │          │          │
     ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────────────┐
│ Library │ │ Student │ │   AI Gateway     │
│Frontend │ │Frontend │ │   SSE 流式对话   │
│ Vue 3   │ │ Vue 3   │ │  Spring WebFlux │
└────┬────┘ └────┬────┘ └────────┬─────────┘
     │          │               │
     ▼          ▼               ▼
┌─────────┐ ┌─────────┐ ┌─────────────────┐
│ Library │ │ Student │ │   通义千问 API    │
│ Backend │ │ Backend │ │  DashScope       │
│ :8080   │ │ :8081   │ │  qwen-plus      │
└────┬────┘ └────┬────┘ └─────────────────┘
     │          │
     ▼          ▼
┌──────────────────────────────┐
│         MySQL 8.0            │
│   library_db + student_db    │
└──────────────────────────────┘
```

## 📦 项目模块

### 📚 图书管理系统

- **JWT 认证授权** — 基于 Spring Security + JWT 的 RBAC 权限控制
- **图书CRUD** — 图书的增删改查、ISBN 唯一校验
- **借阅管理** — 借书/还书流程、借阅历史追踪
- **用户管理** — 管理员可管理用户账号
- **AI 助手** — 右下角悬浮 AI 按钮，支持图书推荐、借阅咨询

### 🎓 学生管理系统

- **学生信息管理** — 学号/姓名/性别/年龄/班级/联系方式
- **数据统计仪表盘** — 总数/性别分布/班级概况
- **AI 数据分析** — 接入通义千问，自然语言查询学生数据
- **响应式 UI** — Vue 3 + Element Plus 现代化界面

### 🤖 AI 智能助手

- **SSE 流式对话** — 基于 Server-Sent Events 的逐字输出，打字机效果
- **会话记忆** — 保留最近 10 轮对话上下文
- **上下文感知** — 在图书页面自动切换"图书馆模式"，在学生页面切换"学生管理模式"
- **快捷提问** — 每个场景预设快捷问题，一键提问

## 🔧 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端** | Spring Boot 2.7 + Spring Security + JPA | RESTful API |
| **认证** | Spring Security + JWT (jjwt 0.12) | 无状态认证 |
| **数据库** | MySQL 8.0 + JPA/Hibernate | 数据持久化 |
| **缓存** | Redis 7 | 会话缓存 |
| **AI** | 通义千问 (DashScope) | OpenAI 兼容 API |
| **流式** | Spring WebFlux + SSE | 逐字流式输出 |
| **前端** | Vue 3 + Vite + Element Plus + Axios | SPA 单页应用 |
| **部署** | Docker Compose + Nginx | 容器化编排 |
| **服务器** | 阿里云 ECS (Ubuntu 22.04) | 2C/2G |

## 🚀 本地运行

```bash
# 1. 克隆项目
git clone https://github.com/hhsdxd/personal-space.git
cd personal-space

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 填入你的 API Key 和数据库密码

# 3. 一键启动
docker compose up -d

# 4. 访问
# http://localhost
```

## 📁 项目结构

```
personal-space/
├── library-management/       # 图书管理系统
│   ├── backend/              # Spring Boot 后端 (21 Java 文件)
│   └── frontend/             # Vue 3 前端 (6 页面)
├── student-management/       # 学生管理系统
│   ├── backend/              # Spring Boot 后端 (19 Java 文件)
│   └── frontend/             # Vue 3 前端 (4 页面)
├── ai-gateway/               # AI 智能网关
│   └── src/                  # SSE 流式 + 会话记忆 + 上下文感知
├── sql/                      # 数据库初始化脚本
├── nginx/                    # Nginx 反向代理配置
├── docker-compose.yml        # Docker 编排文件
├── .env.example              # 环境变量模板
└── README.md
```

## ✨ 亮点

- ✅ **前后端分离** — Spring Boot RESTful API + Vue 3 SPA
- ✅ **AI 大模型集成** — 通义千问实时流式对话
- ✅ **SSE 流式输出** — 打字机效果，用户体验优于同步等待
- ✅ **RBAC 权限控制** — Spring Security + JWT 双拦截器
- ✅ **Docker 容器化** — 9 个服务一键编排部署
- ✅ **在线可访问** — 部署于阿里云 ECS，公网可直接体验
- ✅ **环境变量外部化** — 敏感配置通过 .env 注入，不提交到 Git

## 📝 待优化

- [ ] 升级 Spring Boot 3.x + Java 17
- [ ] 接入向量数据库实现 RAG 知识库问答
- [ ] 集成 Tool Calling（AI 自主查询数据库）
- [ ] 添加单元测试 + 集成测试
- [ ] CI/CD Pipeline (GitHub Actions)

## 📄 License

MIT © [丁嘉明](https://github.com/hhsdxd)
