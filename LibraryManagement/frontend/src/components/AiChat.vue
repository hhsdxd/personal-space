<template>
  <div class="ai-chat" :class="{ open: isOpen }">
    <button class="ai-toggle" @click="toggle" :title="isOpen ? '关闭AI助手' : '打开AI助手'">
      <span v-if="!isOpen">🤖</span>
      <span v-else>✕</span>
    </button>

    <div class="chat-panel" v-if="isOpen">
      <div class="chat-header">
        <h3>🤖 AI 助手</h3>
        <div class="header-actions">
          <span class="model-badge">通义千问</span>
          <button class="icon-btn" @click="clearChat" title="清除对话">🗑</button>
          <button class="icon-btn close-btn" @click="toggle">✕</button>
        </div>
      </div>

      <div class="chat-body" ref="chatBody">
        <div class="msg assistant">
          <div class="msg-content">{{ welcomeMsg }}</div>
        </div>

        <div v-for="(msg, i) in messages" :key="i" class="msg" :class="msg.role">
          <div class="msg-content">{{ msg.content }}</div>
        </div>

        <!-- 流式输出中的消息 -->
        <div v-if="streaming" class="msg assistant">
          <div class="msg-content">
            {{ streamingText }}<span class="cursor">|</span>
          </div>
        </div>
      </div>

      <div class="chat-footer">
        <div class="quick-actions">
          <button v-for="qa in quickActions" :key="qa" @click="quickAsk(qa)" :disabled="loading">
            {{ qa }}
          </button>
        </div>
        <div class="input-row">
          <input
            v-model="input"
            :placeholder="placeholder"
            @keydown.enter="send"
            :disabled="loading"
            ref="inputEl"
          />
          <button v-if="!loading" class="send-btn" @click="send" :disabled="!input.trim()">
            发送
          </button>
          <button v-else class="stop-btn" @click="stopGeneration">
            停止
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const CONTEXTS = {
  student: {
    welcome: '你好！我是学生管理系统的 AI 助手，可以帮你分析学生数据、查找信息、解答问题。试试看吧！',
    quick: ['帮我分析一下当前学生数据', '目前有什么需要关注的问题？', '男女生比例是多少？']
  },
  library: {
    welcome: '你好！我是图书管理系统的 AI 助手，可以帮你推荐图书、查询借阅信息、解答问题。试试看吧！',
    quick: ['推荐几本热门图书', '当前借阅情况如何？', '有哪些书可以借？']
  },
  general: {
    welcome: '你好！我是 AI 智能助手，可以帮你了解这个平台的功能和技术栈。随便聊聊吧！',
    quick: ['介绍一下这个平台', '用了哪些技术栈？', '你有什么功能？']
  }
}

export default {
  name: 'AiChat',
  props: {
    context: { type: String, default: 'general' }
  },
  data() {
    return {
      isOpen: false,
      input: '',
      messages: [],
      streamingText: '',
      loading: false,
      abortController: null,
      sessionId: ''
    }
  },
  computed: {
    ctx() {
      return CONTEXTS[this.context] || CONTEXTS.general
    },
    welcomeMsg() {
      return this.ctx.welcome
    },
    quickActions() {
      return this.ctx.quick
    },
    placeholder() {
      return this.loading ? 'AI 正在回复...' : '输入问题，按 Enter 发送...'
    }
  },
  created() {
    this.sessionId = this.getOrCreateSessionId()
  },
  methods: {
    getOrCreateSessionId() {
      const key = 'ai_session_' + this.context
      let sid = localStorage.getItem(key)
      if (!sid) {
        sid = 'sid_' + Date.now() + '_' + Math.random().toString(36).slice(2, 9)
        localStorage.setItem(key, sid)
      }
      return sid
    },

    toggle() {
      this.isOpen = !this.isOpen
      if (this.isOpen) {
        this.$nextTick(() => {
          this.scrollBottom()
          this.$refs.inputEl?.focus()
        })
      }
    },

    quickAsk(msg) {
      this.input = msg
      this.send()
    },

    async send() {
      const msg = this.input.trim()
      if (!msg || this.loading) return

      this.messages.push({ role: 'user', content: msg })
      this.input = ''
      this.loading = true
      this.streaming = true
      this.streamingText = ''
      this.$nextTick(() => this.scrollBottom())

      this.abortController = new AbortController()

      try {
        const response = await fetch('/api/ai/chat/stream', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            message: msg,
            sessionId: this.sessionId,
            context: this.context
          }),
          signal: this.abortController.signal
        })

        if (!response.ok) {
          throw new Error('HTTP ' + response.status)
        }

        const reader = response.body.getReader()
        const decoder = new TextDecoder()
        let buffer = ''

        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          buffer += decoder.decode(value, { stream: true })
          const lines = buffer.split('\n')
          buffer = lines.pop() || ''

          for (const line of lines) {
            if (line.startsWith('data: ')) {
              const data = line.slice(6).trim()
              if (data === '[DONE]') continue
              if (data === '{"start":true}') continue
              try {
                const parsed = JSON.parse(data)
                if (parsed.content) {
                  this.streamingText += parsed.content
                  this.$nextTick(() => this.scrollBottom())
                }
                if (parsed.error) {
                  this.messages.push({ role: 'assistant', content: '❌ ' + parsed.error })
                }
              } catch (e) {
                // 忽略解析失败的行
              }
            }
          }
        }

        // 流结束，保存最终消息
        if (this.streamingText) {
          this.messages.push({ role: 'assistant', content: this.streamingText })
        }
      } catch (e) {
        if (e.name === 'AbortError') {
          // 用户主动停止
          if (this.streamingText) {
            this.messages.push({ role: 'assistant', content: this.streamingText + ' [已停止]' })
          }
        } else {
          this.messages.push({ role: 'assistant', content: '❌ 网络请求失败，请检查服务是否启动' })
        }
      } finally {
        this.loading = false
        this.streaming = false
        this.streamingText = ''
        this.abortController = null
        this.$nextTick(() => this.scrollBottom())
      }
    },

    stopGeneration() {
      if (this.abortController) {
        this.abortController.abort()
      }
    },

    clearChat() {
      this.messages = []
      this.streamingText = ''
      // 清除后端会话记忆
      fetch('/api/ai/session/' + this.sessionId, { method: 'DELETE' }).catch(() => {})
      // 生成新的 sessionId
      const key = 'ai_session_' + this.context
      localStorage.removeItem(key)
      this.sessionId = this.getOrCreateSessionId()
    },

    scrollBottom() {
      const el = this.$refs.chatBody
      if (el) {
        el.scrollTop = el.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.ai-chat { position: fixed; right: 24px; bottom: 24px; z-index: 9999; }
.ai-toggle {
  width: 56px; height: 56px; border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; border: none; font-size: 22px;
  cursor: pointer; box-shadow: 0 6px 24px rgba(102,126,234,0.45);
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex; align-items: center; justify-content: center;
}
.ai-toggle:hover {
  transform: scale(1.1);
  box-shadow: 0 8px 32px rgba(102,126,234,0.6);
}

.chat-panel {
  position: absolute; right: 0; bottom: 70px;
  width: 400px; height: 560px;
  background: #fff; border-radius: 16px;
  box-shadow: 0 16px 64px rgba(0,0,0,0.18);
  display: flex; flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.25s ease;
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

.chat-header {
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  display: flex; align-items: center; justify-content: space-between;
}
.chat-header h3 { font-size: 15px; font-weight: 600; margin: 0; }
.header-actions { display: flex; align-items: center; gap: 8px; }
.model-badge {
  font-size: 10px; background: rgba(255,255,255,0.22);
  padding: 2px 10px; border-radius: 20px;
}
.icon-btn {
  background: rgba(255,255,255,0.15); border: none; color: #fff;
  width: 26px; height: 26px; border-radius: 50%; cursor: pointer;
  font-size: 12px; display: flex; align-items: center; justify-content: center;
  transition: background 0.15s;
}
.icon-btn:hover { background: rgba(255,255,255,0.3); }

.chat-body {
  flex: 1; overflow-y: auto; padding: 16px;
  background: #f7f8fc;
}
.msg { margin-bottom: 14px; display: flex; }
.msg.user { justify-content: flex-end; }
.msg-content {
  max-width: 82%; padding: 10px 14px; border-radius: 14px;
  font-size: 13px; line-height: 1.6; white-space: pre-wrap; word-break: break-word;
}
.msg.user .msg-content {
  background: linear-gradient(135deg, #667eea, #764ba2); color: #fff;
  border-bottom-right-radius: 4px;
}
.msg.assistant .msg-content {
  background: #fff; color: #333; box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  border-bottom-left-radius: 4px;
}
.cursor {
  animation: blink 0.8s infinite;
  color: #667eea; font-weight: 700;
}
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0; } }

.chat-footer { padding: 12px 16px; background: #fff; border-top: 1px solid #eee; }
.quick-actions { display: flex; gap: 8px; margin-bottom: 10px; flex-wrap: wrap; }
.quick-actions button {
  padding: 5px 14px; font-size: 11px; background: #f0f0ff; color: #667eea;
  border: 1px solid #e0e0ff; border-radius: 16px; cursor: pointer;
  font-weight: 500; transition: all 0.15s; white-space: nowrap;
}
.quick-actions button:hover:not(:disabled) { background: #667eea; color: #fff; border-color: #667eea; }
.quick-actions button:disabled { opacity: 0.5; cursor: not-allowed; }
.input-row { display: flex; gap: 8px; }
.input-row input {
  flex: 1; padding: 10px 14px; border: 2px solid #e8e8e8; border-radius: 10px;
  font-size: 13px; outline: none; transition: border-color 0.2s;
}
.input-row input:focus { border-color: #667eea; }
.send-btn {
  padding: 10px 22px; background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; border: none; border-radius: 10px; font-size: 13px;
  font-weight: 600; cursor: pointer; transition: opacity 0.15s;
}
.send-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.stop-btn {
  padding: 10px 22px; background: #e74c3c; color: #fff;
  border: none; border-radius: 10px; font-size: 13px;
  font-weight: 600; cursor: pointer;
}
</style>
