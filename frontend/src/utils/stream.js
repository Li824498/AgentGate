export class StreamReader {
  constructor(url, options = {}) {
    this.url = url
    this.options = options
    this.controller = null
    this.reader = null
    this.onMessage = null
    this.onError = null
    this.onComplete = null
  }

  start() {
    this.controller = new AbortController()
    const signal = this.controller.signal

    fetch(this.url, {
      ...this.options,
      signal,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'prompt/event-stream',
        ...this.options.headers
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        if (!response.body) {
          throw new Error('Response body is null')
        }
        this.reader = response.body.getReader()
        this.readStream()
      })
      .catch(error => {
        if (error.name === 'AbortError') {
          console.log('Stream aborted')
        } else {
          console.error('Stream error:', error)
          this.onError?.(error)
        }
      })
  }

  async readStream() {
    try {
      const decoder = new TextDecoder()
      let buffer = ''

      while (true) {
        const { done, value } = await this.reader.read()
        
        if (done) {
          if (buffer) {
            try {
              const data = JSON.parse(buffer)
              this.onMessage?.(data)
            } catch (e) {
              console.error('Failed to parse final buffer:', buffer)
            }
          }
          this.onComplete?.()
          break
        }

        // 将新的数据添加到缓冲区
        buffer += decoder.decode(value, { stream: true })
        
        // 处理缓冲区中的完整消息
        const messages = buffer.split('\n')
        buffer = messages.pop() || '' // 保留最后一个不完整的消息

        for (const message of messages) {
          if (message.trim()) {
            try {
              const data = JSON.parse(message)
              this.onMessage?.(data)
            } catch (e) {
              console.error('Failed to parse message:', message)
            }
          }
        }
      }
    } catch (error) {
      console.error('Stream reading error:', error)
      this.onError?.(error)
    }
  }

  stop() {
    if (this.controller) {
      this.controller.abort()
    }
    if (this.reader) {
      this.reader.cancel()
    }
  }

  onMessage(callback) {
    this.onMessage = callback
    return this
  }

  onError(callback) {
    this.onError = callback
    return this
  }

  onComplete(callback) {
    this.onComplete = callback
    return this
  }
} 