<template>
  <div class="immersion-mode-selector">
    <el-dialog
      v-model="dialogVisible"
      title="选择沉浸模式"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-radio-group v-model="selectedMode" class="mode-group">
        <el-radio-button label="default">默认模式</el-radio-button>
        <el-radio-button label="novel">小说模式</el-radio-button>
        <el-radio-button label="group">群聊模式</el-radio-button>
      </el-radio-group>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSelection">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const dialogVisible = ref(false)
const selectedMode = ref('default')

const emit = defineEmits(['mode-selected'])

const show = () => {
  dialogVisible.value = true
}

const confirmSelection = () => {
  emit('mode-selected', selectedMode.value)
  dialogVisible.value = false
  ElMessage.success(`已切换到${getModeName(selectedMode.value)}`)
}

const getModeName = (mode) => {
  const modeNames = {
    default: '默认模式',
    novel: '小说模式',
    group: '群聊模式'
  }
  return modeNames[mode] || '未知模式'
}

defineExpose({
  show
})
</script>

<style scoped>
.immersion-mode-selector {
  position: relative;
}

.mode-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.mode-group :deep(.el-radio-button) {
  width: 100%;
}

.mode-group :deep(.el-radio-button__inner) {
  width: 100%;
  text-align: left;
  padding-left: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 