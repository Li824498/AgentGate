<template>
  <div class="world-book-settings">
    <!-- 世界书选择 -->
    <div class="section">
      <h3>世界书选择</h3>
      <el-form :model="worldBookSelection" label-width="100px">
        <el-form-item label="当前世界书">
          <el-select
            v-model="worldBookSelection.current"
            multiple
            placeholder="请选择世界书"
            @change="handleWorldBookChange"
          >
            <el-option label="不使用世界书" value="none" />
            <el-option
              v-for="book in worldBooks"
              :key="book.id"
              :label="book.name"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 世界书配置 -->
    <div class="section">
      <h3>世界书配置</h3>
      <el-form :model="worldBookConfig" label-width="100px">
        <el-form-item label="世界书文件">
          <el-upload
            class="upload-demo"
            drag
            :http-request="handleUpload"
            :before-upload="beforeUpload"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将世界书文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传JSON格式的世界书文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
    </div>

    <!-- 世界书列表 -->
    <div class="section">
      <h3>世界书列表</h3>
      <div class="world-book-cards">
        <div 
          v-for="book in worldBooks" 
          :key="book.id" 
          class="world-book-card"
        >
          <div class="world-book-header">
            <span class="book-name">{{ book.name }}</span>
            <span class="book-id">#{{ book.id }}</span>
          </div>
          <div class="world-book-content">
            <div class="book-description" v-if="book.description">
              {{ formatDescription(book.description) }}
            </div>
            <div class="book-description empty" v-else>
              暂无描述
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'

const emit = defineEmits(['close'])
const settingsStore = useSettingsStore()

// 世界书选择
const worldBookSelection = ref({
  current: settingsStore.sessionSettings.worldBookIds || []
})

// 世界书列表
const worldBooks = ref([])

// 世界书配置
const worldBookConfig = ref({
  name: '',
  description: ''
})

// 初始化时从store加载
onMounted(() => {
  worldBookSelection.value.current = settingsStore.sessionSettings.worldBookIds || []
  fetchWorldBooks()
})

// 获取世界书列表
const fetchWorldBooks = async () => {
  try {
    const result = await http.get('/api/worldBook')
    if (result.code === 0) {
      worldBooks.value = result.data
    } else {
      ElMessage.error('获取世界书列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取世界书列表失败：' + error.message)
  }
}

// 监听选择变化
watch(worldBookSelection, (newValue) => {
  settingsStore.sessionSettings = {
    ...settingsStore.sessionSettings,
    worldBookIds: newValue.current
  }
}, { deep: true })

// 处理世界书选择变化
const handleWorldBookChange = (value) => {
  worldBookSelection.value.current = value
}

// 上传相关方法
const beforeUpload = (file) => {
  const isJSON = file.type === 'application/json'
  if (!isJSON) {
    ElMessage.error('只能上传JSON文件！')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const result = await http.post('/api/worldBook/upload/file', formData)
    
    if (result.code === 0) {
      ElMessage.success('世界书上传成功')
      // 刷新世界书列表
      fetchWorldBooks()
    } else {
      ElMessage.error('世界书上传失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('世界书上传失败：' + error.message)
  }
}

// 格式化描述
const formatDescription = (description) => {
  if (!description) return ''
  // 限制显示长度，超过部分用省略号
  const maxLength = 100
  return description.length > maxLength 
    ? description.substring(0, maxLength) + '...'
    : description
}
</script>

<style scoped>
.world-book-settings {
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

.upload-demo {
  width: 100%;
}

.world-book-cards {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.world-book-card {
  background-color: white;
  border-radius: 4px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
  transition: all 0.3s;
}

.world-book-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0,0,0,0.2);
}

.world-book-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
}

.book-name {
  font-weight: bold;
  color: #409eff;
}

.book-id {
  color: #909399;
}

.world-book-content {
  min-height: 60px;
}

.book-description {
  color: #606266;
  line-height: 1.5;
  word-break: break-all;
}

.book-description.empty {
  color: #909399;
  font-style: italic;
}
</style> 