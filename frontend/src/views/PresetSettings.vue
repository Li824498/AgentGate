<template>
  <div class="preset-settings">
    <!-- 预设选择 -->
    <div class="section">
      <h3>预设选择</h3>
      <el-form :model="presetSelection" label-width="100px">
        <el-form-item label="当前预设">
          <el-select v-model="presetSelection.current" placeholder="请选择预设">
            <el-option label="不使用预设" value="none" />
            <el-option
              v-for="preset in importedPresets"
              :key="preset.id"
              :label="preset.name"
              :value="preset.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 预设配置 -->
    <div class="section">
      <h3>预设配置</h3>
      <el-form :model="presetConfig" label-width="100px">
        <el-form-item label="预设文件">
          <el-upload
            class="upload-demo"
            drag
            action="/api/presets/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将预设文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传JSON格式的预设文件
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

// 预设选择
const presetSelection = ref({
  current: 'none'
})

// 已导入的预设列表
const importedPresets = ref([
  // 这里应该是从后端获取的预设列表
  // 示例数据
  { id: '1', name: '通用对话预设' },
  { id: '2', name: '代码生成预设' }
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
  ElMessage.success('预设上传成功')
  // 刷新预设列表
  fetchPresets()
}

const handleUploadError = () => {
  ElMessage.error('预设上传失败')
}

// 获取预设列表
const fetchPresets = async () => {
  try {
    const response = await fetch('/api/presets')
    if (response.ok) {
      const data = await response.json()
      importedPresets.value = data
    }
  } catch (error) {
    console.error('获取预设列表失败:', error)
  }
}

// 初始化时获取预设列表
fetchPresets()
</script>

<style scoped>
.preset-settings {
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