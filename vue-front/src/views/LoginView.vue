<script setup lang="ts">
import { container } from 'tsyringe'
import SocialLoginRepository from '@/repository/SocialLoginRepository.ts'
import { onMounted, reactive } from 'vue'
import NaverLoginParams from '@/request/NaverLoginParams'
import KakaoLoginParams from '@/request/KakaoLoginParams'
import { useSocialEnv } from '@/config/useSocialEnv.ts'

const SOCIAL_LOGIN_REPOSITORY = container.resolve(SocialLoginRepository)
const { socialEnv } = useSocialEnv()

type StateType = {
  naverLoginParams: NaverLoginParams
  kakooLoginParams: KakaoLoginParams
}

const states = reactive<StateType>({
  naverLoginParams: new NaverLoginParams(),
  kakooLoginParams: new KakaoLoginParams(),
})

function redirectToKakaoLogin() {
  window.location.href = `https://kauth.kakao.com/oauth/authorize?client_id=${socialEnv.kakao_rest_api_key}&redirect_uri=${socialEnv.redirect_uri}&response_type=code`
}

function redirectToNaverLogin() {
  window.location.href = `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${socialEnv.naver_client_id}&redirect_uri=${socialEnv.redirect_uri}&state=test`
}

onMounted(() => {
  const query = new URLSearchParams(window.location.search)
  const code = query.get('code')
  const state = query.get('state')

  if (code && state) {
    states.naverLoginParams.state = state
    states.naverLoginParams.code = code
    naverLogin(states.naverLoginParams)
  } else if (code) {
    states.kakooLoginParams.code = code
    kakaoLogin(states.kakooLoginParams)
  }
})

function kakaoLogin(params: KakaoLoginParams) {
  SOCIAL_LOGIN_REPOSITORY.kakaoLogin(params)
}

function naverLogin(params: NaverLoginParams) {
  SOCIAL_LOGIN_REPOSITORY.naverLogin(params)
}
</script>

<template>
  <div class="social_div d-flex flex-column align-items-center">
    <div>
      <h2>소셜 로그인</h2>
    </div>
    <div class="kakao_btn_div">
      <button class="kakao_login_btn d-flex justify-content-center" @click="redirectToKakaoLogin()">
        <img src="/kakao_login.png" alt="kakao_login" class="kakao_login_img" />
      </button>
    </div>
    <div class="naver_btn_div">
      <button class="naver_login_btn" @click="redirectToNaverLogin()">
        <img src="/naver_login.png" alt="naver_login" class="naver_login_img" />
      </button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.kakao_btn_div {
  display: flex;
  justify-content: center;
}

.kakao_login_btn {
  padding: 0;
  margin: 0;
  border: none;
  background: none;
  line-height: 0;
  display: inline-block;
}

.kakao_login_img {
  display: block;
  width: 250px; /* 너비 조절 */
  height: auto;
}

.naver_btn_div {
  display: flex;
  justify-content: center;
}

.naver_login_btn {
  padding: 0;
  margin: 10px 0 0 0;
  border: none;
  background: none;
  line-height: 0;
  display: inline-block;
}

.naver_login_img {
  display: block;
  width: 250px; /* 너비 조절 */
  height: auto;
}
</style>
