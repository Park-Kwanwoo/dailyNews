import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // state
  const accessToken = ref(localStorage.getItem('accessToken') || '')

  // actions
  function setToken(token: string) {
    accessToken.value = token
    localStorage.setItem('accessToken', token)
  }

  function isLoggedIn() {
    return accessToken.value != '' && localStorage.getItem('accessToken') != ''
  }

  function logout() {
    accessToken.value = null
    localStorage.setItem('accessToken', '')
  }

  return {
    accessToken,
    setToken,
    isLoggedIn,
    logout,
  }
})
