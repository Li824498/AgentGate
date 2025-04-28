<template>
  <div class="stream-container">
    <div class="stream-content">
      <div v-for="(message, index) in messages" :key="index" class="message">
        {{ message }}
      </div>
    </div>
    <div class="stream-controls">
      <button @click="startStream" :disabled="isStreaming">开始流式传输</button>
      <button @click="stopStream" :disabled="!isStreaming">停止流式传输</button>
    </div>
  </div>
</template>

<script>
import { StreamReader } from '../utils/stream'

export default {
  name: 'StreamComponent',
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
      this.messages = []
      
      this.streamReader = new StreamReader('/api/stream')
        .onMessage(data => {
          this.messages.push(data.content)
        })
        .onError(error => {
          console.error('Stream error:', error)
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
    }
  },
  beforeUnmount() {
    this.stopStream()
  }
}
</script>

<style scoped>
.stream-container {
  padding: 20px;
}

.stream-content {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
}

.message {
  margin-bottom: 5px;
  padding: 5px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.stream-controls {
  display: flex;
  gap: 10px;
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
</style> 