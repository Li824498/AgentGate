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
    // 直接返回响应数据
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
      return result
    } catch (error) {
      return httpInterceptor.error(error)
    }
  },
  
  post: async (url, data, options = {}) => {
    // 处理FormData
    let body
    let headers = {
      'Content-Type': 'application/json',
      ...options.headers
    }

    if (data instanceof FormData) {
      body = data
      // 删除Content-Type，让浏览器自动设置boundary
      delete headers['Content-Type']
    } else {
      body = JSON.stringify(data)
    }

    const config = httpInterceptor.request({
      method: 'POST',
      headers,
      body,
      ...options
    })

    try {
      const response = await fetch(url, config)
      const result = await response.json()
      return result
    } catch (error) {
      return httpInterceptor.error(error)
    }
  }
} 