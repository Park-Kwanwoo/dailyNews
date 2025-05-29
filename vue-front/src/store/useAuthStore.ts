import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // state
  const accessToken = ref<string | null>(null)

  // actions
  function setToken(token: string) {
    accessToken.value = token
  }

  function isLoggedIn() {
    return accessToken.value != null && accessToken.value != ''
  }

  function logout() {
    accessToken.value = null
  }

  return {
    accessToken,
    setToken,
    isLoggedIn,
    logout,
  }
})
