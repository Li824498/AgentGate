<template>
  <div class="chat-container">
    <div class="left-panel">
      <el-button
        type="primary"
        circle
        :icon="ChatDotRound"
        @click="showImmersionModeSelector"
      />
    </div>
    <div class="chat-content">
      <div class="messages" ref="messagesContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="{ 'user-message': message.role === 'user' }"
        >
          <pre>{{ JSON.stringify(message, null, 2) }}</pre>
        </div>
      </div>
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="3"
          placeholder="输入消息..."
          @keyup.enter.ctrl="sendMessage"
        />
        <el-button type="primary" @click="sendMessage">发送</el-button>
      </div>
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
    </div>
    <ImmersionModeSelector ref="immersionModeSelector" @mode-selected="handleModeSelected" />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ChatDotRound, View, Setting } from '@element-plus/icons-vue'
import ImmersionModeSelector from '@/components/ImmersionModeSelector.vue'

const router = useRouter()
const route = useRoute()

const isChatView = computed(() => route.path === '/')
const messages = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)
const immersionModeSelector = ref(null)
const currentMode = ref('default')

const showImmersionModeSelector = () => {
  immersionModeSelector.value.show()
}

const handleModeSelected = (mode) => {
  currentMode.value = mode
  // TODO: 根据不同的模式切换显示方式
}

const switchToChat = () => {
  router.push('/')
}

const switchToDebug = () => {
  router.push('/debug')
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

  // TODO: 发送消息到后端
}

onMounted(() => {
  // 初始化消息容器滚动到底部
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.left-panel {
  position: fixed;
  left: 20px;
  top: 20px;
  z-index: 1000;
}

.left-panel .el-button {
  width: 50px;
  height: 50px;
  font-size: 20px;
  border-radius: 50%;
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

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 70px;
  margin-right: 70px;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.message {
  margin-bottom: 20px;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-message {
  background-color: #e6f7ff;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}

.active {
  background-color: #409eff;
  border-color: #409eff;
}
</style> 