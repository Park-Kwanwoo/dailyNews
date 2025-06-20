<script setup lang="ts">
import User from '@/entity/User.ts'
import { computed, onMounted, reactive } from 'vue'
import { SOCIAL_AUTH_REPOSITORY } from '@/repository/httpProviders.ts'
import router from '@/router'
import { useAuthStore } from '@/store/useAuthStore.ts'

type StateType = {
  user: User
}

const authStore = useAuthStore()
const accessToken = authStore.getToken()
const isLoggedIn = computed(() => authStore.isLoggedIn())

const state = reactive<StateType>({
  user: new User(),
})

onMounted(() => {
  if (!isLoggedIn.value) {
    router.replace('/')
  } else {
    SOCIAL_AUTH_REPOSITORY.getUserInfo(accessToken).then((user) => {
      state.user = user
    })
  }
})
</script>

<template>
  <el-card class="mypage-user-info" shadow="never" style="max-width: 400px; margin: 0 auto">
    <el-descriptions border column="1" size="default">
      <el-descriptions-item label="이메일">
        {{ state.user.email }}
      </el-descriptions-item>
      <el-descriptions-item label="닉네임">
        {{ state.user.nickname }}
      </el-descriptions-item>
      <el-descriptions-item label="소셜 로그인">
        <el-tag v-if="state.user.provider === 'KAKAO'" type="warning" effect="plain">카카오</el-tag>
        <el-tag v-else-if="state.user.provider === 'NAVER'" type="success" effect="plain"
          >네이버</el-tag
        >
        <el-tag v-else type="info" effect="plain">{{ state.user.provider }}</el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>

<style scoped lang="scss"></style>
