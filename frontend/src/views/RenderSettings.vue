<template>
  <div class="render-settings">
    <!-- 渲染模式选择 -->
    <div class="section">
      <h3>渲染模式选择</h3>
      <el-form :model="renderSelection" label-width="100px">
        <el-form-item label="当前模式">
          <el-select v-model="renderSelection.current" placeholder="请选择渲染模式">
            <el-option label="不使用渲染" value="none" />
            <el-option
              v-for="render in importedRenders"
              :key="render.id"
              :label="render.name"
              :value="render.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 渲染配置 -->
    <div class="section">
      <h3>渲染配置</h3>
      <el-form :model="renderConfig" label-width="100px">
        <el-form-item label="渲染文件">
          <el-upload
            class="upload-demo"
            drag
            action="/api/renders/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将渲染文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传JSON格式的渲染配置文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 渲染模式选择
const renderSelection = ref({
  current: 'none'
})

// 已导入的渲染模式列表
const importedRenders = ref([
  // 这里应该是从后端获取的渲染模式列表
  // 示例数据
  { id: '1', name: 'Markdown渲染' },
  { id: '2', name: '代码高亮渲染' }
])

// 上传相关方法
const beforeUpload = (file) => {
  const isJSON = file.type === 'application/json'
  if (!isJSON) {
    ElMessage.error('只能上传JSON文件！')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  ElMessage.success('渲染配置上传成功')
  // 刷新渲染模式列表
  fetchRenders()
}

const handleUploadError = () => {
  ElMessage.error('渲染配置上传失败')
}

// 获取渲染模式列表
const fetchRenders = async () => {
  try {
    const response = await fetch('/api/renders')
    if (response.ok) {
      const data = await response.json()
      importedRenders.value = data
    }
  } catch (error) {
    console.error('获取渲染模式列表失败:', error)
  }
}

// 初始化时获取渲染模式列表
fetchRenders()
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

.el-form {
  max-width: 500px;
}

.el-select {
  width: 100%;
}

.upload-demo {
  width: 100%;
}
</style> 