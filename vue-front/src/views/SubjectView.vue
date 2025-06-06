<script setup lang="ts">
import { container } from 'tsyringe'
import SubjectRepository from '@/repository/SubjectRepository.ts'
import { computed, onMounted, reactive } from 'vue'
import { useAuthStore } from '@/store/useAuthStore'
import Subject from '@/entity/Subject.ts'
import SubjectRequest from '@/request/SubjectRequest.ts'
import router from '@/router'

const SUBJECT_REPOSITORY = container.resolve(SubjectRepository)
const authStore = useAuthStore()
const accessToken = authStore.accessToken
const isLoggedIn = computed(() => authStore.isLoggedIn())

type StateType = {
  subject: Subject
  subjectRequest: SubjectRequest
}

const state = reactive<StateType>({
  subject: new Subject(),
  subjectRequest: new SubjectRequest(),
})

function registerSubject() {
  SUBJECT_REPOSITORY.registerSubject(state.subjectRequest, accessToken)
}

onMounted(() => {
  if (!isLoggedIn) {
    router.replace('/social')
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
      placeholder="새로운 주제를 입력하세요"
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
