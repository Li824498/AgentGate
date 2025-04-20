<template>
  <div class="model-settings">
    <!-- 模型选择 -->
    <div class="section">
      <h3>模型选择</h3>
      <el-form :model="modelSelection" label-width="100px">
        <el-form-item label="模型类型">
          <el-select v-model="modelSelection.type" placeholder="请选择模型类型">
            <el-option label="Gemini" value="gemini" />
            <el-option label="GPT" value="gpt" />
            <el-option label="Claude" value="claude" />
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
            <el-option label="Gemini" value="gemini" />
            <el-option label="GPT" value="gpt" />
            <el-option label="Claude" value="claude" />
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
import { ref } from 'vue'

// 模型选择
const modelSelection = ref({
  type: ''
})

// 模型配置
const modelConfig = ref({
  type: '',
  api: ''
})

const importModel = async () => {
  if (!modelConfig.value.type || !modelConfig.value.api) {
    ElMessage.warning('请填写完整的模型配置信息')
    return
  }

  try {
    const response = await fetch('/api/models', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(modelConfig.value)
    })
    
    if (response.ok) {
      ElMessage.success('模型导入成功')
      modelConfig.value = {
        type: '',
        api: ''
      }
    } else {
      throw new Error('导入失败')
    }
  } catch (error) {
    ElMessage.error('模型导入失败：' + error.message)
  }
}
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