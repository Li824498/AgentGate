import { defineStore } from 'pinia'

export const useSettingsStore = defineStore('settings', {
  state: () => ({
    modelSettings: {
      modelName: '',
      api: '',
      temperature: 0.7,
      maxTokens: 2000
    },
    presetSettings: {
      preset: '',
      customPrompt: ''
    },
    characterSettings: {
      character: '',
      personality: '',
      background: ''
    },
    sessionSettings: {
      sessionId: '',
      contextLength: 10,
      worldBookIds: []
    },
    renderSettings: {
      suite: 'default',
      renders: ['不使用渲染']
    }
  }),
  persist: {
    key: 'user-settings',
    storage: localStorage
  }
}) 