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
      contextLength: 10
    },
    renderSettings: {
      theme: 'default',
      fontSize: 14,
      spacing: 1.5
    }
  }),
  persist: {
    key: 'user-settings',
    storage: localStorage
  }
}) 