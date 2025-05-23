<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useAuthStore } from '@/store/useAuthStore'
import { container } from 'tsyringe'
import SubjectRepository from '@/repository/SubjectRepository.ts'
import Subject from '@/request/Subject'

type StateType = {
  subject: Subject
}

const SUBJECT_REPOSITORY = container.resolve(SubjectRepository)

const authStore = useAuthStore()
const state = reactive<StateType>({
  subject: new Subject(),
})

function register() {
  SUBJECT_REPOSITORY.registerSubject(state.subject, authStore.accessToken)
}

onMounted(() => {
  SUBJECT_REPOSITORY.getSubjects(authStore.accessToken)
})
</script>

<template>
  <div class="justify-content-center">
    <div class="d-flex justify-content-center">
      <h2>알림받고자 하는 주제를 입력해주세요.</h2>
    </div>
  </div>

  <div class="justify-content-center">
    <div class="d-flex justify-content-end gap-2">
      <el-input type="text" v-model="state.subject.keyword" placeholder="주제를 입력해주세요." />
      <el-button type="primary" @click="register">설정</el-button>
    </div>
  </div>

  <div class="justify-content-center">
    <h2>뉴스 목록</h2>
  </div>
</template>

<style scoped lang="scss"></style>
