<template>
  <div class="stream-debugger">
    <div class="stream-header">
      <h2>流式调试界面</h2>
      <div class="stream-controls">
        <button @click="startStream" :disabled="isStreaming">开始流式传输</button>
        <button @click="stopStream" :disabled="!isStreaming">停止流式传输</button>
        <button @click="clearMessages">清空消息</button>
      </div>
    </div>
    
    <div class="stream-content">
      <div class="message-list">
        <div v-for="(message, index) in messages" :key="index" class="message">
          <div class="message-header">
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
            <span class="message-type">{{ message.type }}</span>
          </div>
          <div class="message-body">
            <pre>{{ JSON.stringify(message.content, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { StreamReader } from '../utils/stream'
import axios from 'axios'

export default {
  name: 'StreamDebugger',
  data() {
    return {
      messages: [],
      isStreaming: false,
      streamReader: null
    }
  },
  methods: {
    startStream() {
      this.isStreaming = true
      
      const requestData = {
        // 这里添加你的请求参数
        prompt: "测试流式传输",
        model: "gpt-3.5-turbo"
      }

      this.streamReader = new StreamReader('/api/chat/syncStream', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
      })
        .onMessage(data => {
          this.messages.push({
            content: data,
            type: 'response',
            timestamp: new Date()
          })
        })
        .onError(error => {
          console.error('Stream error:', error)
          this.messages.push({
            content: error.message,
            type: 'error',
            timestamp: new Date()
          })
          this.isStreaming = false
        })
        .onComplete(() => {
          this.isStreaming = false
        })
      
      this.streamReader.start()
    },
    stopStream() {
      if (this.streamReader) {
        this.streamReader.stop()
        this.isStreaming = false
      }
    },
    clearMessages() {
      this.messages = []
    },
    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString()
    }
  },
  beforeUnmount() {
    this.stopStream()
  }
}
</script>

<style scoped>
.stream-debugger {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.stream-header {
  margin-bottom: 20px;
}

.stream-header h2 {
  margin-bottom: 10px;
}

.stream-controls {
  display: flex;
  gap: 10px;
}

.stream-content {
  flex: 1;
  overflow: hidden;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.message-list {
  height: 100%;
  overflow-y: auto;
  padding: 10px;
}

.message {
  margin-bottom: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.message-header {
  padding: 8px;
  background-color: #f5f5f5;
  display: flex;
  justify-content: space-between;
  font-size: 0.9em;
}

.message-body {
  padding: 10px;
  background-color: white;
}

.message-body pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

button {
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.message-type {
  font-weight: bold;
  color: #666;
}
</style> 