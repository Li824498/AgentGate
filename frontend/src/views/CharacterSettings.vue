<template>
  <div class="character-settings">
    <!-- 角色卡选择 -->
    <div class="section">
      <h3>角色卡选择</h3>
      <el-form :model="characterSelection" label-width="100px">
        <el-form-item label="当前角色">
          <el-select v-model="characterSelection.current" placeholder="请选择角色">
            <el-option label="不使用角色" value="none" />
            <el-option
              v-for="character in importedCharacters"
              :key="character.id"
              :label="character.name"
              :value="character.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 角色卡配置 -->
    <div class="section">
      <h3>角色卡配置</h3>
      <el-form :model="characterConfig" label-width="100px">
        <el-form-item label="角色卡文件">
          <el-upload
            class="upload-demo"
            drag
            action="/api/characters/upload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将角色卡文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传JSON格式的角色卡文件
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

const settingsStore = useSettingsStore()

// 角色卡选择
const characterSelection = ref({
  current: settingsStore.characterSettings.character || 'none'
})

// 已导入的角色卡列表
const importedCharacters = ref([
  // 这里应该是从后端获取的角色卡列表
  // 示例数据
  { id: '1', name: '助手' },
  { id: '2', name: '程序员' }
])

// 角色卡配置
const characterConfig = ref({
  character: settingsStore.characterSettings.character || '',
  personality: settingsStore.characterSettings.personality || '',
  background: settingsStore.characterSettings.background || ''
})

// 监听变化并更新store
watch(characterConfig, (newValue) => {
  settingsStore.characterSettings = {
    character: newValue.character,
    personality: newValue.personality,
    background: newValue.background
  }
}, { deep: true })

// 监听选择变化
watch(characterSelection, (newValue) => {
  settingsStore.characterSettings = {
    ...settingsStore.characterSettings,
    character: newValue.current
  }
}, { deep: true })

// 初始化时从store加载
onMounted(() => {
  characterSelection.value.current = settingsStore.characterSettings.character || 'none'
  characterConfig.value.character = settingsStore.characterSettings.character
  characterConfig.value.personality = settingsStore.characterSettings.personality
  characterConfig.value.background = settingsStore.characterSettings.background
})

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
  ElMessage.success('角色卡上传成功')
  // 刷新角色卡列表
  fetchCharacters()
}

const handleUploadError = () => {
  ElMessage.error('角色卡上传失败')
}

// 获取角色卡列表
const fetchCharacters = async () => {
  try {
    const response = await fetch('/api/characters')
    if (response.ok) {
      const data = await response.json()
      importedCharacters.value = data
    }
  } catch (error) {
    console.error('获取角色卡列表失败:', error)
  }
}

// 初始化时获取角色卡列表
fetchCharacters()
</script>

<style scoped>
.character-settings {
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