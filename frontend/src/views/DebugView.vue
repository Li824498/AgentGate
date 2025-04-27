<template>
  <div class="debug-container">
    <el-container>
      <!-- 左侧导航栏 -->
      <el-aside width="200px">
        <el-menu
          default-active="1"
          class="el-menu-vertical"
          @select="handleSelect">
          <el-menu-item index="1">
            <el-icon><Setting /></el-icon>
            <span>模型设置</span>
          </el-menu-item>
          <el-menu-item index="2">
            <el-icon><Document /></el-icon>
            <span>预设设置</span>
          </el-menu-item>
          <el-menu-item index="3">
            <el-icon><User /></el-icon>
            <span>角色卡设置</span>
          </el-menu-item>
          <el-menu-item index="4">
            <el-icon><ChatLineRound /></el-icon>
            <span>会话选择设置</span>
          </el-menu-item>
          <el-menu-item index="5">
            <el-icon><Collection /></el-icon>
            <span>世界书设置</span>
          </el-menu-item>
          <el-menu-item index="6">
            <el-icon><Picture /></el-icon>
            <span>渲染设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-main>
          <!-- 聊天窗口 -->
          <div class="chat-container">
            <div class="chat-box" ref="chatBox">
              <div v-for="(message, index) in messages" :key="index" class="message" :class="message.role">
                <div class="message-header">
                  <span class="role">{{ message.role === 'user' ? '用户' : 'AI' }}</span>
                  <span class="time">{{ formatTime(message.createTime) }}</span>
                </div>
                <div class="message-content">
                  <span>{{ message.context }}</span>
                </div>
                <div v-if="message.renderedList && message.renderedList.length > 0" class="rendered-list">
                  <div v-for="(rendered, rIndex) in message.renderedList" :key="rIndex" class="rendered-item">
                    <div class="rendered-type">{{ rendered.renderType }}</div>
                    <div class="rendered-content">
                      <pre>{{ rendered.outContext }}</pre>
                    </div>
                  </div>
                </div>
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

          <!-- 设置浮层 -->
          <el-dialog
            v-model="dialogVisible"
            :title="currentDialogTitle"
            width="70%"
            :before-close="handleClose"
          >
            <component :is="currentComponent" @close="handleClose" />
          </el-dialog>
        </el-main>
        
        <!-- 右侧导航菜单 -->
        <el-aside width="60px" class="right-aside">
          <div class="nav-buttons">
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
        </el-aside>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, shallowRef, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Setting,
  Document,
  User,
  ChatLineRound,
  Picture,
  View,
  SwitchButton,
  Collection
} from '@element-plus/icons-vue'
import ModelSettings from './ModelSettings.vue'
import PresetSettings from './PresetSettings.vue'
import CharacterSettings from './CharacterSettings.vue'
import SessionSettings from './SessionSettings.vue'
import RenderSettings from './RenderSettings.vue'
import WorldBookSettings from './WorldBookSettings.vue'
import { useSettingsStore } from '@/stores/settings'
import { ElMessage } from 'element-plus'
import { http } from '@/utils/http'

const router = useRouter()
const route = useRoute()
const settingsStore = useSettingsStore()

// 状态
const isChatView = computed(() => route.path === '/')
const dialogVisible = ref(false)
const currentDialogTitle = ref('')
const currentComponent = shallowRef(null)
const messages = ref([])
const inputMessage = ref('')
const chatBox = ref(null)

// 组件映射
const components = {
  '1': ModelSettings,
  '2': PresetSettings,
  '3': CharacterSettings,
  '4': SessionSettings,
  '5': WorldBookSettings,
  '6': RenderSettings
}

const titles = {
  '1': '模型设置',
  '2': '预设设置',
  '3': '角色卡设置',
  '4': '会话选择设置',
  '5': '世界书设置',
  '6': '渲染设置'
}

// 方法
const handleSelect = (key) => {
  currentDialogTitle.value = titles[key]
  currentComponent.value = components[key]
  dialogVisible.value = true
}

const handleClose = () => {
  if (currentComponent.value) {
    const settingsKey = Object.keys(components).find(key => components[key] === currentComponent.value)
    if (settingsKey) {
      const storeKey = `${settingsKey}Settings`
      settingsStore[storeKey] = currentComponent.value.settings || {}
    }
  }
  dialogVisible.value = false
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const currentMsgIndex = settingsStore.sessionSettings.msgIndex
  const request = {
    context: inputMessage.value,
    userId: localStorage.getItem('userId'),
    chatId: settingsStore.sessionSettings.currentChatId,
    promptId: settingsStore.presetSettings.preset ? parseInt(settingsStore.presetSettings.preset) : 0,
    roleCardId: settingsStore.characterSettings.character ? parseInt(settingsStore.characterSettings.character) : 0,
    msgIndex: currentMsgIndex,
    worldBookIds: settingsStore.sessionSettings.worldBookIds || [],
    renders: settingsStore.renderSettings.renders || ['不使用渲染'],
    modelName: settingsStore.modelSettings.modelName,
    api: settingsStore.modelSettings.api
  }

  // 添加用户消息
  const userMessage = {
    role: 'user',
    context: inputMessage.value,
    createTime: new Date().toISOString()
  }
  
  messages.value = [...messages.value, userMessage]
  inputMessage.value = ''

  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }

  try {
    const result = await http.post('/api/chat', request)
    if (result.code === 0) {
      // 添加AI响应
      const assistantMessage = {
        role: 'assistant',
        context: result.data.inContext || result.data.context,
        createTime: new Date().toISOString(),
        renderedList: result.data.outContext ? [{
          renderType: '输出',
          outContext: result.data.outContext
        }] : []
      }
      
      messages.value = [...messages.value, assistantMessage]
      
      // 更新消息索引
      settingsStore.sessionSettings = {
        ...settingsStore.sessionSettings,
        msgIndex: currentMsgIndex + 2
      }
      
      await nextTick()
      if (chatBox.value) {
        chatBox.value.scrollTop = chatBox.value.scrollHeight
      }
    } else {
      ElMessage.error('发送消息失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('发送消息失败：' + error.message)
  }
}

const fetchHistories = async (chatId) => {
  try {
    const result = await http.get(`/api/chat/${chatId}`)
    if (result.code === 0) {
      messages.value = result.data.map(item => ({
        role: item.historyMessage.role,
        context: item.historyMessage.inContext || item.historyMessage.context,
        createTime: item.historyMessage.createTime,
        renderedList: item.historyRenderedList || (item.historyMessage.outContext ? [{
          renderType: '输出',
          outContext: item.historyMessage.outContext
        }] : [])
      }))
      
      await nextTick()
      if (chatBox.value) {
        chatBox.value.scrollTop = chatBox.value.scrollHeight
      }
    } else {
      ElMessage.error('获取历史消息失败：' + result.message)
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

const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString()
}

// 监听会话变化
watch(() => settingsStore.sessionSettings.currentChatId, (newChatId) => {
  if (newChatId) {
    fetchHistories(newChatId)
  }
}, { immediate: true })
</script>

<style scoped>
.debug-container {
  height: 100vh;
  background-color: #f5f7fa;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
}

.right-aside {
  border-right: none;
  border-left: 1px solid #e6e6e6;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 10px;
  background-color: #fff;
}

.nav-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  align-items: center;
}

.nav-buttons .el-button {
  width: 40px;
  height: 40px;
  padding: 0;
  border-radius: 4px;
  margin: 0;
}

.nav-buttons .el-button i {
  font-size: 18px;
}

.el-menu {
  border-right: none;
}

.el-main {
  padding: 20px;
  background-color: #fff;
  margin: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.active {
  background-color: #409eff;
  border-color: #409eff;
}

.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-box {
  flex: 1;
  overflow-y: auto;
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.message {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 8px;
  background-color: #f5f7fa;
  border: 1px solid #e6e6e6;
  font-size: 14px;
  line-height: 1.5;
}

.message.user {
  background-color: #e6f7ff;
  margin-left: 20%;
  border-color: #91d5ff;
}

.message.assistant {
  background-color: #f0f9eb;
  margin-right: 20%;
  border-color: #b3e19d;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.message-header .role {
  font-weight: bold;
  color: #409eff;
}

.message-header .time {
  color: #909399;
}

.message-content {
  color: #303133;
  line-height: 1.5;
  font-size: 14px;
  white-space: pre-wrap;
  word-wrap: break-word;
  padding: 5px 0;
}

.rendered-list {
  margin-top: 10px;
  border-top: 1px solid #eee;
  padding-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.rendered-item {
  background-color: #fdf6ec;
  padding: 15px;
  border-radius: 4px;
  border: 1px solid #faecd8;
}

.rendered-type {
  font-size: 14px;
  color: #e6a23c;
  margin-bottom: 10px;
  font-weight: bold;
  padding-bottom: 5px;
  border-bottom: 1px solid #faecd8;
}

.rendered-content {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
  border: 1px solid #ebeef5;
}

.rendered-content pre {
  margin: 0;
  padding: 0;
  font-family: inherit;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area .el-input {
  flex: 1;
}
</style> 