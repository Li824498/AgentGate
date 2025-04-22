<template>
  <div class="session-settings">
    <!-- 会话选择 -->
    <div class="section">
      <h3>会话选择</h3>
      <el-form :model="sessionSelection" label-width="100px">
        <el-form-item label="当前会话">
          <el-select v-model="sessionSelection.current" placeholder="请选择会话">
            <el-option label="新建会话" value="new" />
            <el-option
              v-for="session in sessions"
              :key="session.id"
              :label="formatSessionLabel(session)"
              :value="session.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 会话配置 -->
    <div class="section">
      <h3>会话配置</h3>
      <el-form>
        <el-form-item>
          <el-button type="primary" @click="createSession">创建会话</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 会话列表 -->
    <div class="section">
      <h3>会话列表</h3>
      <div class="session-cards">
        <div 
          v-for="session in sessions" 
          :key="session.id" 
          class="session-card"
          @click="selectSession(session)"
        >
          <div class="session-header">
            <span class="role-name">{{ session.roleCardName || '无角色' }}</span>
            <span class="chat-id">#{{ session.chatId }}</span>
            <span class="create-time">{{ formatDate(session.createTime) }}</span>
            <span class="message-count">{{ session.msgNum || 0 }} 条消息</span>
          </div>
          <div class="session-content">
            <div class="last-history" v-if="session.lastHistory">
              {{ formatLastHistory(session.lastHistory) }}
            </div>
            <div class="last-history empty" v-else>
              暂无消息
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'

const emit = defineEmits(['close'])
const settingsStore = useSettingsStore()

// 会话选择
const sessionSelection = ref({
  current: settingsStore.sessionSettings.currentSession || 'new'
})

// 会话列表
const sessions = ref([])

const sessionForm = ref({
  sessionId: settingsStore.sessionSettings.sessionId || '',
  contextLength: settingsStore.sessionSettings.contextLength || 10
})

// 初始化时从store加载
onMounted(() => {
  sessionSelection.value.current = settingsStore.sessionSettings.currentSession || 'new'
  sessionForm.value.sessionId = settingsStore.sessionSettings.sessionId
  sessionForm.value.contextLength = settingsStore.sessionSettings.contextLength
  fetchSessions()
})

// 创建会话
const createSession = () => {
  // 获取最后一个会话的chatId
  const lastSession = sessions.value.length > 0 ? sessions.value[sessions.value.length - 1] : null
  const newChatId = lastSession ? (parseInt(lastSession.chatId) + 1).toString() : '1'
  
  // 保存新的chatId到store
  settingsStore.sessionSettings = {
    ...settingsStore.sessionSettings,
    currentChatId: newChatId
  }
  
  // 关闭浮窗
  emit('close')
}

// 选择会话
const selectSession = (session) => {
  // 更新全局状态
  settingsStore.sessionSettings = {
    ...settingsStore.sessionSettings,
    currentChatId: session.chatId,
    roleCardId: session.roleCardId,
    msgIndex: (session.msgNum || 0) + 1
  }
  
  // 关闭浮窗
  emit('close')
}

// 切换会话
const switchSession = (session) => {
  sessionSelection.value.current = session.id
  settingsStore.sessionSettings = {
    ...settingsStore.sessionSettings,
    sessionId: session.id
  }
  ElMessage.success(`已切换到会话：${session.name}`)
}

// 删除会话
const deleteSession = (session) => {
  const index = sessions.value.findIndex(s => s.id === session.id)
  if (index !== -1) {
    sessions.value.splice(index, 1)
    ElMessage.success('会话删除成功')
  }
}

// 格式化会话标签
const formatSessionLabel = (session) => {
  const date = new Date(session.updateTime)
  return `${session.chatId} (${date.toLocaleString()})`
}

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

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 格式化最后一条消息
const formatLastHistory = (history) => {
  if (!history) return ''
  // 限制显示长度，超过部分用省略号
  const maxLength = 100
  return history.length > maxLength 
    ? history.substring(0, maxLength) + '...'
    : history
}
</script>

<style scoped>
.session-settings {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.section h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.el-form {
  max-width: 500px;
}

.el-select {
  width: 100%;
}

.session-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.session-card {
  background-color: white;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  cursor: pointer;
  transition: all 0.3s;
}

.session-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0,0,0,0.2);
}

.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.role-name {
  font-weight: bold;
  color: #409eff;
}

.chat-id {
  color: #909399;
}

.create-time {
  color: #909399;
}

.message-count {
  color: #67c23a;
}

.session-content {
  min-height: 60px;
}

.last-history {
  color: #606266;
  line-height: 1.5;
  word-break: break-all;
}

.last-history.empty {
  color: #909399;
  font-style: italic;
}
</style> 