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
              :label="session.name"
              :value="session.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 会话配置 -->
    <div class="section">
      <h3>会话配置</h3>
      <el-form :model="sessionConfig" label-width="100px">
        <el-form-item label="会话名称">
          <el-input v-model="sessionConfig.name" placeholder="请输入会话名称" />
        </el-form-item>
        <el-form-item label="会话描述">
          <el-input
            v-model="sessionConfig.description"
            type="textarea"
            :rows="3"
            placeholder="请输入会话描述"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="createSession">创建会话</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 会话列表 -->
    <div class="section">
      <h3>会话列表</h3>
      <el-table :data="sessions" style="width: 100%">
        <el-table-column prop="name" label="会话名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column prop="messageCount" label="消息数量" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" link @click="switchSession(scope.row)">
              切换
            </el-button>
            <el-button type="danger" link @click="deleteSession(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 模拟数据
const mockSessions = [
  {
    id: '1',
    name: '默认会话',
    description: '系统默认会话',
    createdAt: '2024-03-20 10:00:00',
    messageCount: 10
  },
  {
    id: '2',
    name: '测试会话',
    description: '用于测试的会话',
    createdAt: '2024-03-21 14:30:00',
    messageCount: 5
  }
]

// 会话选择
const sessionSelection = ref({
  current: 'new'
})

// 会话配置
const sessionConfig = ref({
  name: '',
  description: ''
})

// 会话列表
const sessions = ref(mockSessions)

// 创建会话
const createSession = async () => {
  if (!sessionConfig.value.name) {
    ElMessage.warning('请输入会话名称')
    return
  }

  // 模拟创建会话
  const newSession = {
    id: String(Date.now()),
    name: sessionConfig.value.name,
    description: sessionConfig.value.description,
    createdAt: new Date().toLocaleString(),
    messageCount: 0
  }

  sessions.value.push(newSession)
  sessionConfig.value = {
    name: '',
    description: ''
  }
  ElMessage.success('会话创建成功')
}

// 切换会话
const switchSession = (session) => {
  sessionSelection.value.current = session.id
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

// TODO: 等后端接口完成后，替换为实际的API调用
// const fetchSessions = async () => {
//   try {
//     const response = await fetch('/api/sessions')
//     if (response.ok) {
//       const data = await response.json()
//       sessions.value = data
//     }
//   } catch (error) {
//     console.error('获取会话列表失败:', error)
//   }
// }
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
</style> 