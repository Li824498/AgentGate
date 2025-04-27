<template>
  <div class="chat-container">
    <div class="chat-content">
      <div class="messages"></div>
    </div>
    <div class="right-panel">
      <el-button
        type="primary"
        :icon="View"
        @click="switchToChat"
        :class="{ active: isChatView }"
      />
      <el-button
        type="primary"
        :icon="Setting"
        @click="switchToDebug"
        :class="{ active: !isChatView }"
      />
      <el-button
        type="danger"
        :icon="SwitchButton"
        @click="handleLogout"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { View, Setting, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'

const router = useRouter()
const route = useRoute()
const settingsStore = useSettingsStore()

const isChatView = computed(() => route.path === '/')
const messages = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)

// 获取历史消息
const fetchHistory = async () => {
  try {
    const result = await http.get('/api/chat/history', {
      params: {
        chatId: settingsStore.sessionSettings.currentChatId
      }
    })
    if (result.code === 0) {
      messages.value = result.data.map(item => ({
        role: item.historyMessage.role,
        content: item.historyMessage.context,
        renderedList: item.historyRenderedList
      }))
    }
  } catch (error) {
    ElMessage.error('获取历史消息失败：' + error.message)
  }
}

const switchToChat = () => {
  router.push('/')
}

const switchToDebug = () => {
  router.push('/debug')
}

const handleLogout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('password')
  router.push('/login')
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const userMessage = {
    role: 'user',
    content: inputMessage.value
  }
  messages.value.push(userMessage)
  inputMessage.value = ''

  await nextTick()
  messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight

  // 发送消息到后端
  const request = {
    context: inputMessage.value,
    userId: localStorage.getItem('userId'),
    chatId: settingsStore.sessionSettings.currentChatId,
    promptId: settingsStore.presetSettings.preset ? parseInt(settingsStore.presetSettings.preset) : 0,
    roleCardId: settingsStore.characterSettings.character ? parseInt(settingsStore.characterSettings.character) : 0,
    msgIndex: settingsStore.sessionSettings.msgIndex,
    worldBookIds: settingsStore.sessionSettings.worldBookIds || [],
    renders: settingsStore.renderSettings.renders || ['不使用渲染'],
    modelName: settingsStore.modelSettings.modelName,
    api: settingsStore.modelSettings.api
  }

  try {
    const result = await http.post('/api/chat', request)
    if (result.code === 0) {
      // 处理响应
      messages.value.push({
        role: 'assistant',
        content: result.data.context,
        renderedList: result.data.historyRenderedList
      })
      // 更新消息索引
      settingsStore.sessionSettings.msgIndex += 2
    } else {
      ElMessage.error('发送消息失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('发送消息失败：' + error.message)
  }
}

onMounted(() => {
  // 初始化消息容器滚动到底部
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
  // 获取历史消息
  fetchHistory()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 70px;
  margin-right: 70px;
}

.messages {
  flex: 1;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.right-panel {
  position: fixed;
  right: 20px;
  top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 1000;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.right-panel .el-button {
  width: 40px;
  height: 40px;
  padding: 0;
  border-radius: 4px;
  margin: 0;
}

.right-panel .el-button i {
  font-size: 18px;
}

.active {
  background-color: #409eff;
  border-color: #409eff;
}
</style> 