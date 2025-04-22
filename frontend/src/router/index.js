import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/components/LoginView.vue'
import ChatView from '@/views/ChatView.vue'
import DebugView from '@/views/DebugView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      name: 'chat',
      component: ChatView,
      meta: { requiresAuth: true }
    },
    {
      path: '/debug',
      name: 'debug',
      component: DebugView,
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router 