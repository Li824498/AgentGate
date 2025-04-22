<template>
  <div class="model-settings">
    <!-- 模型选择 -->
    <div class="section">
      <h3>模型选择</h3>
      <el-form :model="modelSelection" label-width="100px">
        <el-form-item label="模型类型">
          <el-select v-model="modelSelection.type" placeholder="请选择模型类型">
            <el-option
              v-for="model in modelList"
              :key="model"
              :label="model"
              :value="model"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 模型配置 -->
    <div class="section">
      <h3>模型配置</h3>
      <el-form :model="modelConfig" label-width="100px">
        <el-form-item label="模型类型">
          <el-select v-model="modelConfig.type" placeholder="请选择模型类型">
            <el-option
              v-for="model in modelList"
              :key="model"
              :label="model"
              :value="model"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="API地址">
          <el-input v-model="modelConfig.api" placeholder="请输入API地址" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="importModel">导入</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useSettingsStore } from '@/stores/settings'
import { ElMessage } from 'element-plus'
import { http } from '@/utils/http'

const settingsStore = useSettingsStore()

// 模型列表
const modelList = ref([])

// 模型选择
const modelSelection = ref({
  type: settingsStore.modelSettings.modelName || ''
})

// 模型配置
const modelConfig = ref({
  type: '',
  api: ''
})

// 获取模型列表
const fetchModels = async () => {
  try {
    const result = await http.get('/api/model/query')
    if (result.code === 0) {
      modelList.value = result.data
    } else {
      ElMessage.error('获取模型列表失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('获取模型列表失败：' + error.message)
  }
}

// 导入模型
const importModel = async () => {
  if (!modelConfig.value.type || !modelConfig.value.api) {
    ElMessage.warning('请填写完整的模型配置信息')
    return
  }

  try {
    const result = await http.post('/api/model/save', {
      model: modelConfig.value.type,
      api: modelConfig.value.api
    })
    
    if (result.code === 0) {
      ElMessage.success('模型导入成功')
      modelConfig.value = {
        type: '',
        api: ''
      }
      // 刷新模型列表
      fetchModels()
    } else {
      ElMessage.error('模型导入失败：' + result.message)
    }
  } catch (error) {
    ElMessage.error('模型导入失败：' + error.message)
  }
}

// 监听模型选择变化
watch(modelSelection, (newValue) => {
  settingsStore.modelSettings = {
    modelName: newValue.type,
    api: ''
  }
}, { deep: true })

// 初始化时获取模型列表
onMounted(() => {
  fetchModels()
})
</script>

<style scoped>
.model-settings {
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