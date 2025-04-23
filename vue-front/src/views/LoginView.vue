<script setup lang="ts">
import { container } from 'tsyringe'
import SocialLoginRepository from '@/repository/SocialLoginRepository.ts'
import { onMounted } from 'vue'

const SOCIAL_LOGIN_REPOSITORY = container.resolve(SocialLoginRepository)
const REST_API_KEY = import.meta.env.VITE_KAKAO_REST_API_KEY
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI

function kakaoLogin() {
  window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${KAKAO_REDIRECT_URI}&response_type=code`
}

onMounted(() => {
  const query = new URLSearchParams(window.location.search)
  const code = query.get('code')
  if (code) {
    login(code)
  }
})

function login(code) {
  SOCIAL_LOGIN_REPOSITORY.login(code)
}
</script>

<template>
  <div class="social_div d-flex flex-column align-items-center">
    <div>
      <h2>소셜 로그인</h2>
    </div>
    <div class="kakao_btn_div">
      <button class="kakao_login_btn d-flex justify-content-center" @click="kakaoLogin()">
        <img src="/kakao_login.png" alt="kakao_login" class="kakao_login_img" />
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.kakao_login_btn {
  background: transparent;
  border: none;
  padding: 0;
  margin: 0;
  cursor: pointer;
}

.kakao_login_img {
  display: block;
  width: 80%;
  height: auto;
}
</style>
