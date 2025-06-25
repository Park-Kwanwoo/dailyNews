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
  editMode: boolean
}

const state = reactive<StateType>({
  subject: new Subject(),
  subjectRequest: new SubjectRequest(),
  editMode: false,
})

function isValidKoreanSentence(str: string) {
  return !/^[가-힣\s]+$/.test(str.trim())
}

function registerSubject() {
  if (state.subjectRequest.keyword == '' || isValidKoreanSentence(state.subjectRequest.keyword)) {
    ElMessage.error('한글로된 주제를 입력해주세요.')
  } else {
    SUBJECT_REPOSITORY.save(state.subjectRequest, accessToken)
  }
}

function editSubject() {
  if (state.subjectRequest.keyword == '' || isValidKoreanSentence(state.subjectRequest.keyword)) {
    ElMessage.error('한글로된 주제를 입력해주세요.')
  } else {
    SUBJECT_REPOSITORY.update(state.subjectRequest, accessToken)
  }
}

onMounted(() => {
  if (state.subject.keyword != '') {
  }

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
  <div>
    <el-card class="subject-card" shadow="hover">
      <div class="current-subject" style="text-align: center; margin-bottom: 24px">
        <div v-if="state.subject.keyword == ''">
          <strong style="font-size: 1.5rem">주제가 등록되어 있지 않습니다.</strong>
          <el-input
            v-model="state.subjectRequest.keyword"
            placeholder="부적절한 주제는 뉴스 생성이 어려울 수 있습니다."
            style="margin: 0 auto 12px"
          />
          <el-button
            type="primary"
            @click="registerSubject()"
            style="width: 100px; height: 48px; margin: 16px auto 0; display: block"
          >
            등록
          </el-button>
        </div>

        <div v-else>
          <div style="font-size: 1.5rem; margin-bottom: 18px">
            현재 주제: <b>{{ state.subject.keyword }}</b>
            <el-button
              v-if="!state.editMode"
              type="success"
              size="small"
              @click="state.editMode = true"
              style="margin-left: 16px"
            >
              수정
            </el-button>
          </div>

          <div v-if="state.editMode">
            <el-input
              v-model="state.subjectRequest.keyword"
              placeholder="주제를 수정하세요"
              style="margin: 0 auto 12px"
            />
            <div style="display: flex; justify-content: center; gap: 12px; margin-bottom: 8px">
              <el-button type="primary" @click="editSubject()">저장</el-button>
              <el-button @click="state.editMode = false">취소</el-button>
            </div>
          </div>
        </div>
      </div>
      <el-divider style="margin-top: 30px" />
    </el-card>
  </div>
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
