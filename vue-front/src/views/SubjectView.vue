<script setup lang="ts">
import { SUBJECT_REPOSITORY } from '@/repository/httpProviders.ts'

import { computed, onMounted, reactive } from 'vue'
import { useAuthStore } from '@/store/useAuthStore'
import Subject from '@/entity/Subject.ts'
import SubjectRequest from '@/request/SubjectRequest.ts'
import router from '@/router'
import { ElMessage } from 'element-plus'
const authStore = useAuthStore()
const accessToken = authStore.getToken()
const isLoggedIn = computed(() => authStore.isLoggedIn())

type StateType = {
  subject: Subject
  subjectRequest: SubjectRequest
}

const state = reactive<StateType>({
  subject: new Subject(),
  subjectRequest: new SubjectRequest(),
})

function isValidKoreanSentence(str: string) {
  return /^[가-힣\s]+$/.test(str.trim())
}

function registerSubject() {
  if (state.subjectRequest.keyword == '' || isValidKoreanSentence(state.subjectRequest.keyword)) {
    ElMessage.error('한글로된 주제를 입력해주세요.')
  } else {
    SUBJECT_REPOSITORY.registerSubject(state.subjectRequest, accessToken)
  }
}

onMounted(() => {
  if (!isLoggedIn.value) {
    router.replace('/')
  } else {
    SUBJECT_REPOSITORY.getSubjects(accessToken).then((subject) => {
      state.subject = subject
    })
  }
})
</script>

<template>
  <el-card class="subject-card" shadow="hover">
    <div class="current-subject">
      <div v-if="state.subject.keyword == ''">
        <strong>주제가 등록되어 있지 않습니다.</strong>
      </div>
      <div v-else>
        <span>현재 주제: </span>
        <strong>{{ state.subject.keyword }}</strong>
      </div>
    </div>
    <el-divider />
    <el-input
      v-model="state.subjectRequest.keyword"
      placeholder="부적절한 주제는 뉴스 생성이 어려울 수 있습니다."
      class="mb-2"
    />
    <el-button type="primary" @click="registerSubject()" style="float: right">변경</el-button>
  </el-card>
</template>

<style scoped lang="scss">
.subject-card {
  max-width: 400px;
  margin: 60px auto;
  padding: 24px 16px;
}

.current-subject {
  font-size: 18px;
  margin-bottom: 12px;
  text-align: center;
}
</style>
