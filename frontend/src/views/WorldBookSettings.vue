<template>
  <div class="world-book-settings">
    <!-- 世界书选择 -->
    <div class="section">
      <h3>世界书选择</h3>
      <div class="world-book-table">
        <el-table
          :data="worldBooks"
          style="width: 100%"
          max-height="400"
          :row-class-name="tableRowClassName"
        >
          <el-table-column width="50">
            <template #default="{ row }">
              <el-checkbox
                v-model="row.selected"
                @change="handleWorldBookChange(row)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="名称" min-width="120" />
          <el-table-column prop="description" label="描述" min-width="200">
            <template #default="{ row }">
              <div class="description-cell">
                {{ formatDescription(row.description) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'DONE' ? 'success' : 'warning'">
                {{ row.status === 'DONE' ? '就绪' : '解析中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
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
            :on-progress="handleProgress"
            :on-success="handleSuccess"
            :on-error="handleError"
            :show-file-list="false"
            accept=".json,.txt,.pdf"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将世界书文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持上传JSON、TXT、PDF格式的世界书文件，文件大小不超过10MB
              </div>
            </template>
          </el-upload>
          <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
            <el-progress :percentage="uploadProgress" :format="progressFormat" />
          </div>
        </el-form-item>
      </el-form>
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

// 世界书列表
const worldBooks = ref([])

// 世界书配置
const worldBookConfig = ref({
  name: '',
  description: ''
})

// 上传进度
const uploadProgress = ref(0)

// 初始化时从store加载
onMounted(() => {
  fetchWorldBooks()
})

// 获取世界书列表
const fetchWorldBooks = async () => {
  try {
    const result = await http.get('/api/worldBook')
    if (result.code === 0) {
      // 为每个世界书添加selected属性，并根据当前选中的ID设置
      const selectedIds = settingsStore.sessionSettings.worldBookIds || []
      worldBooks.value = result.data.map(book => ({
        ...book,
        selected: selectedIds.includes(book.id)
      }))
    } else {
      ElMessage.error('获取世界书列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取世界书列表失败：' + error.message)
  }
}

// 处理世界书选择变化
const handleWorldBookChange = (row) => {
  const selectedIds = worldBooks.value
    .filter(book => book.selected)
    .map(book => book.id)
  
  settingsStore.sessionSettings = {
    ...settingsStore.sessionSettings,
    worldBookIds: selectedIds
  }
}

// 上传相关方法
const beforeUpload = (file) => {
  const allowedTypes = ['application/json', 'prompt/plain', 'application/pdf']
  const isAllowedType = allowedTypes.includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isAllowedType) {
    ElMessage.error('只能上传JSON、TXT或PDF文件！')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB！')
    return false
  }
  return true
}

const handleProgress = (event) => {
  uploadProgress.value = Math.floor((event.loaded / event.total) * 100)
}

const handleSuccess = () => {
  uploadProgress.value = 0
  ElMessage.success('世界书上传成功')
  fetchWorldBooks()
}

const handleError = (error) => {
  uploadProgress.value = 0
  ElMessage.error('世界书上传失败：' + (error.message || '未知错误'))
}

const progressFormat = (percentage) => {
  return percentage === 100 ? '上传完成' : `${percentage}%`
}

const handleUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const result = await http.post('/api/worldBook/upload/file', formData, {
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.floor((progressEvent.loaded * 100) / progressEvent.total)
        uploadProgress.value = percentCompleted
      }
    })
    
    if (result.code === 0) {
      handleSuccess()
    } else {
      handleError({ message: result.message })
    }
  } catch (error) {
    handleError(error)
  }
}

// 格式化描述
const formatDescription = (description) => {
  if (!description) return '暂无描述'
  // 限制显示长度，超过部分用省略号
  const maxLength = 100
  return description.length > maxLength 
    ? description.substring(0, maxLength) + '...'
    : description
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString()
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  return row.status === 'PARSING' ? 'parsing-row' : ''
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

.world-book-table {
  margin-top: 20px;
}

.description-cell {
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

:deep(.parsing-row) {
  background-color: #fdf6ec;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

.upload-demo {
  width: 100%;
}

.upload-progress {
  margin-top: 10px;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style> 