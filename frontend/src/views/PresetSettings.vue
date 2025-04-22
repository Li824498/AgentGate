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
              v-for="preset in presetList"
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
            :http-request="handleUpload"
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
import { ref, onMounted, watch } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useSettingsStore } from '@/stores/settings'
import { http } from '@/utils/http'

const settingsStore = useSettingsStore()

// 预设列表
const presetList = ref([])

// 预设选择
const presetSelection = ref({
  current: settingsStore.presetSettings.preset || 'none'
})

// 预设配置
const presetConfig = ref({
  preset: '',
  customPrompt: ''
})

// 获取预设列表
const fetchPresets = async () => {
  try {
    const result = await http.get('/api/prompts')
    if (result.code === 0) {
      presetList.value = result.data
    } else {
      ElMessage.error('获取预设列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取预设列表失败：' + error.message)
  }
}

// 监听预设选择变化
watch(presetSelection, (newValue) => {
  settingsStore.presetSettings = {
    preset: newValue.current,
    customPrompt: ''
  }
}, { deep: true })

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
    const result = await http.post('/api/prompts/upload/file', formData)
    
    if (result.code === 0) {
      ElMessage.success('预设上传成功')
      // 刷新预设列表
      fetchPresets()
    } else {
      ElMessage.error('预设上传失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('预设上传失败：' + error.message)
  }
}

// 初始化时获取预设列表
onMounted(() => {
  fetchPresets()
})
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