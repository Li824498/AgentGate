// 创建请求拦截器
const httpInterceptor = {
  // 请求拦截
  request: (config) => {
    // 获取token
    const token = localStorage.getItem('token')
    if (token) {
      // 添加token到请求头
      config.headers = {
        ...config.headers,
        'token': token
      }
    }
    return config
  },
  
  // 响应拦截
  response: (response) => {
    return response
  },
  
  // 错误处理
  error: (error) => {
    return Promise.reject(error)
  }
}

// 封装fetch请求
export const http = {
  get: async (url, options = {}) => {
    const config = httpInterceptor.request({
      method: 'GET',
      ...options
    })
    try {
      const response = await fetch(url, config)
      const result = await response.json()
      return httpInterceptor.response(result)
    } catch (error) {
      return httpInterceptor.error(error)
    }
  },
  
  post: async (url, data, options = {}) => {
    const config = httpInterceptor.request({
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      },
      body: JSON.stringify(data),
      ...options
    })
    try {
      const response = await fetch(url, config)
      const result = await response.json()
      return httpInterceptor.response(result)
    } catch (error) {
      return httpInterceptor.error(error)
    }
  }
} 