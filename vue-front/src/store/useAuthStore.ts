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

  function getToken() {
    return accessToken.value == '' || undefined || null
      ? localStorage.getItem('accessToken')
      : accessToken.value
  }

  function isLoggedIn() {
    return accessToken.value != '' && localStorage.getItem('accessToken') != ''
  }

  function logout() {
    accessToken.value = ''
    localStorage.setItem('accessToken', '')
  }

  return {
    accessToken,
    setToken,
    getToken,
    isLoggedIn,
    logout,
  }
})
