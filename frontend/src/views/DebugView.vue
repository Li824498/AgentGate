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
            <el-icon><Picture /></el-icon>
            <span>渲染设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-main>
          <!-- 默认显示聊天窗口 -->
          <div class="chat-container">
            <div class="chat-box" ref="chatBox">
              <div v-for="(message, index) in messages" :key="index" class="message">
                <pre>{{ JSON.stringify(message, null, 2) }}</pre>
              </div>
            </div>
            <div class="input-area">
              <el-input
                v-model="inputMessage"
                type="textarea"
                :rows="3"
                placeholder="输入消息..."
                @keyup.enter.native="sendMessage"
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
            <component :is="currentComponent" />
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
import { ref, computed, shallowRef } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Setting,
  Document,
  User,
  ChatLineRound,
  Picture,
  View,
  SwitchButton
} from '@element-plus/icons-vue'
import ModelSettings from './ModelSettings.vue'
import PresetSettings from './PresetSettings.vue'
import CharacterSettings from './CharacterSettings.vue'
import SessionSettings from './SessionSettings.vue'
import RenderSettings from './RenderSettings.vue'

const router = useRouter()
const route = useRoute()

const isChatView = computed(() => route.path === '/')
const dialogVisible = ref(false)
const currentDialogTitle = ref('')
const currentComponent = shallowRef(null)

const messages = ref([])
const inputMessage = ref('')
const chatBox = ref(null)

const components = {
  '1': ModelSettings,
  '2': PresetSettings,
  '3': CharacterSettings,
  '4': SessionSettings,
  '5': RenderSettings
}

const titles = {
  '1': '模型设置',
  '2': '预设设置',
  '3': '角色卡设置',
  '4': '会话选择设置',
  '5': '渲染设置'
}

const handleSelect = (key) => {
  currentDialogTitle.value = titles[key]
  currentComponent.value = components[key]
  dialogVisible.value = true
}

const handleClose = () => {
  dialogVisible.value = false
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  const message = {
    context: inputMessage.value,
    userId: "1",
    chatId: "1",
    msgIndex: messages.value.length + 1,
    modelName: "gemini",
    api: "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyBdRTvIyopn0zc1z_uenRPVzO8cMapm_pI"
  }

  messages.value.push(message)
  inputMessage.value = ''

  try {
    const response = await fetch('/api/chat', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(message)
    })
    const data = await response.json()
    messages.value.push(data)
  } catch (error) {
    messages.value.push({
      error: error.message
    })
  }

  await nextTick()
  chatBox.value.scrollTop = chatBox.value.scrollHeight
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
  margin-bottom: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.message pre {
  margin: 0;
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