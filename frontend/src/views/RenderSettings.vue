<template>
  <div class="render-settings">
    <!-- 渲染套件选择 -->
    <div class="section">
      <h3>渲染套件选择</h3>
      <div class="suite-list">
        <div 
          v-for="suite in suiteList" 
          :key="suite.id" 
          class="suite-item"
          @click="selectSuite(suite)"
        >
          <div class="suite-header">
            <span class="suite-name">{{ suite.name }}</span>
            <span class="suite-time">{{ formatTime(suite.updateTime) }}</span>
          </div>
          <div class="suite-description">{{ suite.description }}</div>
          <div class="render-list">
            <div 
              v-for="(renderName, renderId) in suite.renderMap" 
              :key="renderId"
              class="render-item"
            >
              {{ renderName }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建新套件按钮 -->
    <div class="section">
      <el-button type="primary" @click="showCreateDialog">创建新套件</el-button>
    </div>

    <!-- 创建套件对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建渲染套件"
      width="50%"
    >
      <el-form :model="newSuite" label-width="100px">
        <el-form-item label="套件名称">
          <el-input v-model="newSuite.name" placeholder="请输入套件名称" />
        </el-form-item>
        <el-form-item label="套件描述">
          <el-input
            v-model="newSuite.description"
            type="textarea"
            :rows="3"
            placeholder="请输入套件描述"
          />
        </el-form-item>
        <el-form-item label="渲染组件">
          <div class="render-selector">
            <el-table
              :data="availableRenders"
              style="width: 100%"
              height="300"
              @selection-change="handleSelectionChange"
            >
              <el-table-column type="selection" width="55" />
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="组件名称" />
              <el-table-column prop="description" label="描述" />
            </el-table>
              </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="createSuite">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'

const settingsStore = useSettingsStore()

// 渲染套件列表
const suiteList = ref([])

// 可用的渲染组件列表
const availableRenders = ref([])

// 创建套件对话框
const createDialogVisible = ref(false)
const newSuite = ref({
  name: '',
  description: '',
  renderMap: {}
})

// 获取渲染套件列表
const fetchSuites = async () => {
  try {
    const result = await http.get('/api/suiteRender')
    if (result.code === 0) {
      suiteList.value = result.data
    } else {
      ElMessage.error('获取渲染套件列表失败：' + result.message)
}
  } catch (error) {
    ElMessage.error('获取渲染套件列表失败：' + error.message)
  }
}

// 获取可用的渲染组件
const fetchRenders = async () => {
  try {
    const result = await http.get('/api/render')
    if (result.code === 0) {
      // 确保每个渲染组件都有完整的属性
      availableRenders.value = result.data.map(render => ({
        id: render.id,
        name: render.name,
        description: render.description,
        text: render.text,
        createTime: render.createTime,
        updateTime: render.updateTime,
        selected: false
      }))
      console.log('Available renders:', availableRenders.value) // 调试用
    } else {
      ElMessage.error('获取渲染组件列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取渲染组件列表失败：' + error.message)
  }
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  console.log('Selected renders:', selection) // 调试用
  // 更新选中状态
  availableRenders.value.forEach(render => {
    render.selected = selection.some(item => item.id === render.id)
  })
}

// 选择套件
const selectSuite = (suite) => {
  settingsStore.renderSettings = {
    suite: suite.id,
    renders: Object.values(suite.renderMap)
  }
  ElMessage.success(`已选择渲染套件：${suite.name}`)
}

// 显示创建对话框
const showCreateDialog = () => {
  createDialogVisible.value = true
  newSuite.value = {
    name: '',
    description: '',
    renderMap: {}
  }
}

// 创建套件
const createSuite = async () => {
  if (!newSuite.value.name) {
    ElMessage.warning('请输入套件名称')
    return
  }

  // 获取选中的渲染组件
  const selectedRenders = availableRenders.value.filter(render => render.selected)
  console.log('Selected renders for creation:', selectedRenders) // 调试用
  
  if (selectedRenders.length === 0) {
    ElMessage.warning('请至少选择一个渲染组件')
    return
  }

  // 构建renderMap，确保使用正确的id和name
  const renderMap = {}
  selectedRenders.forEach(render => {
    if (render.id && render.name) {
      renderMap[render.id] = render.name
    }
  })
  console.log('Built renderMap:', renderMap) // 调试用

  // 验证renderMap是否构建成功
  if (Object.keys(renderMap).length === 0) {
    ElMessage.error('渲染组件数据不完整，请检查')
    return
  }

  try {
    const requestData = {
      name: newSuite.value.name,
      description: newSuite.value.description,
      renderMap: renderMap
    }
    console.log('Request data:', requestData) // 调试用
    
    const result = await http.post('/api/suiteRender', requestData)
    if (result.code === 0) {
      ElMessage.success('创建渲染套件成功')
      createDialogVisible.value = false
      fetchSuites()
    } else {
      ElMessage.error('创建渲染套件失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('创建渲染套件失败：' + error.message)
  }
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString()
}

// 初始化
onMounted(() => {
  fetchSuites()
  fetchRenders()
})
</script>

<style scoped>
.render-settings {
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

.suite-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.suite-item {
  padding: 15px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.suite-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.suite-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.suite-name {
  font-weight: bold;
  color: #303133;
}

.suite-time {
  font-size: 12px;
  color: #909399;
}

.suite-description {
  color: #606266;
  margin-bottom: 10px;
  font-size: 14px;
}

.render-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.render-item {
  padding: 4px 8px;
  background-color: #f4f4f5;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}

.render-selector {
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  padding: 10px;
}
</style> 