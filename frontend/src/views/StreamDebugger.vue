<template>
  <div class="stream-debugger">
    <div class="stream-header">
      <h2>流式调试界面</h2>
      <div class="stream-controls">
        <el-form :model="formData" label-width="100px">
          <!-- 会话选择 -->
          <el-form-item label="选择会话">
            <el-select 
              v-model="formData.currentChatId" 
              placeholder="请选择会话"
              @change="handleSessionChange"
            >
              <el-option
                v-for="session in sessions"
                :key="session.chatId"
                :label="formatSessionLabel(session)"
                :value="session.chatId"
              />
            </el-select>
          </el-form-item>

          <!-- 消息输入区域 -->
          <el-form-item label="消息">
            <div class="input-area">
              <el-input
                v-model="formData.prompt"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keyup.enter.ctrl="sendMessage"
              />
              <el-button 
                type="primary" 
                @click="sendMessage"
                :disabled="!formData.currentChatId"
              >
                发送消息
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
    
    <div class="stream-content">
      <div class="message-list">
        <div v-for="(message, index) in messages" :key="index" class="message">
          <div class="message-header">
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
            <span class="message-type">{{ message.type }}</span>
          </div>
          <div class="message-body">
            <pre>{{ message.content }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { StreamReader } from '../utils/stream'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'
import { ElMessage } from 'element-plus'

const settingsStore = useSettingsStore()

const messages = ref([])
const streamReader = ref(null)
const sessions = ref([])

const formData = ref({
  currentChatId: '',
  prompt: ''
})

// 获取会话列表
const fetchSessions = async () => {
  try {
    const result = await http.get('/api/chatMeta')
    if (result.code === 0) {
      sessions.value = result.data
    } else {
      ElMessage.error('获取会话列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取会话列表失败：' + error.message)
  }
}

// 处理会话选择变化
const handleSessionChange = (chatId) => {
  if (chatId) {
    settingsStore.sessionSettings = {
      ...settingsStore.sessionSettings,
      currentChatId: chatId
    }
    ElMessage.success('已选择会话')
  }
}

const sendMessage = () => {
  if (!formData.value.currentChatId) {
    ElMessage.warning('请先选择会话')
    return
  }

  if (!formData.value.prompt.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  const requestData = {
    context: formData.value.prompt,
    userId: localStorage.getItem('userId'),
    chatId: formData.value.currentChatId,
    sessionId: settingsStore.sessionSettings.sessionId,
    model: settingsStore.modelSettings.modelName,
    temperature: settingsStore.renderSettings.temperature,
    maxTokens: settingsStore.renderSettings.maxTokens,
    topP: settingsStore.renderSettings.topP,
    frequencyPenalty: settingsStore.renderSettings.frequencyPenalty,
    presencePenalty: settingsStore.renderSettings.presencePenalty
  }

  console.log('Sending request:', requestData)

  // 创建新的 StreamReader 实例
  const reader = new StreamReader('/api/chat/syncStream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Token': localStorage.getItem('token')
    },
    body: JSON.stringify(requestData)
  })

  // 设置回调函数
  reader.onMessage = (data) => {
    console.log('Received message:', data)
    // 如果是第一条消息，创建一个新的消息对象
    if (!messages.value.length || messages.value[messages.value.length - 1].type !== 'assistant') {
      messages.value.push({
        content: data.inContext || '',
        type: 'assistant',
        timestamp: new Date()
      })
    } else {
      // 否则追加到上一条消息的内容中
      const lastMessage = messages.value[messages.value.length - 1]
      lastMessage.content += data.inContext || ''
    }
  }

  reader.onError = (error) => {
    console.error('Stream error:', error)
    ElMessage.error('流式响应出错：' + error.message)
  }

  reader.onComplete = () => {
    console.log('Stream completed')
    formData.value.prompt = ''
  }

  // 启动流式读取
  reader.start()
  streamReader.value = reader
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString()
}

const formatSessionLabel = (session) => {
  const date = new Date(session.updateTime)
  return `${session.chatId} (${date.toLocaleString()})`
}

// 初始化
onMounted(() => {
  fetchSessions()
  // 如果有当前会话，设置到表单中
  if (settingsStore.sessionSettings.currentChatId) {
    formData.value.currentChatId = settingsStore.sessionSettings.currentChatId
  }
})
</script>

<style scoped>
.stream-debugger {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.stream-header {
  margin-bottom: 20px;
}

.stream-header h2 {
  margin-bottom: 10px;
}

.stream-controls {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}

.stream-content {
  flex: 1;
  overflow: hidden;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-top: 20px;
}

.message-list {
  height: 100%;
  overflow-y: auto;
  padding: 10px;
}

.message {
  margin-bottom: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.message.user {
  background-color: #e6f7ff;
  border-color: #91d5ff;
}

.message.assistant {
  background-color: #f0f9eb;
  border-color: #b3e19d;
}

.message.error {
  background-color: #fef0f0;
  border-color: #fde2e2;
}

.message-header {
  padding: 8px;
  background-color: #f5f5f5;
  display: flex;
  justify-content: space-between;
  font-size: 0.9em;
}

.message-body {
  padding: 10px;
  background-color: white;
}

.message-body pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.message-type {
  font-weight: bold;
  color: #666;
}
</style> 