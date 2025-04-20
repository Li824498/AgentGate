<template>
  <div class="login-container">
    <div class="login-box">
      <h2>场景隔离登录</h2>
      <el-form :model="loginForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input v-model="loginForm.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({
  userId: '',
  password: ''
})

const handleLogin = () => {
  if (!loginForm.value.userId || !loginForm.value.password) {
    ElMessage.warning('请输入用户ID和密码')
    return
  }
  
  // 这里可以添加实际的登录验证逻辑
  // 暂时简单处理，直接存储到localStorage
  localStorage.setItem('userId', loginForm.value.userId)
  localStorage.setItem('password', loginForm.value.password)
  
  ElMessage.success('登录成功')
  router.push('/')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-box {
  width: 400px;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.el-form {
  margin-top: 20px;
}

.el-button {
  width: 100%;
}
</style> 