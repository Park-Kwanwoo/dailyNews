<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useAuthStore } from '@/store/useAuthStore'
import { container } from 'tsyringe'
import Subject from '@/entity/Subject.ts'
import NewsRepository from '@/repository/NewsRepository.ts'
import Paging from '@/entity/Paging.ts'
import News from '@/entity/News.ts'
import router from '@/router'

type StateType = {
  subject: Subject
  newsList: Paging<News>
}
const NEWS_REPOSITORY = container.resolve(NewsRepository)

const authStore = useAuthStore()
const accessToken = authStore.getToken()
const isLoggedIn = computed(() => authStore.isLoggedIn())

const state = reactive<StateType>({
  subject: new Subject(),
  newsList: new Paging<News>(),
})

function getList(page = 1) {
  NEWS_REPOSITORY.getList(page, accessToken).then((newsList) => {
    state.newsList = newsList
  })
}

onMounted(() => {
  if (!isLoggedIn.value) {
    router.replace('/')
  } else {
    getList()
  }
})
</script>

<template>
  <div class="justify-content-center">
    <h2>뉴스 목록</h2>
  </div>

  <div class="d-flex justify-content-center">
    <div class="content w-50">
      <span class="totalCount">게시글 수: {{ state.newsList.totalCount }}</span>

      <ul class="news">
        <li v-for="news in state.newsList.items" :key="news.id">
          <h3>
            <router-link :to="{ path: `/news/${news.id}` }">{{ news.title }}</router-link>
          </h3>
        </li>
      </ul>

      <div class="d-flex justify-content-center">
        <el-pagination
          size="small"
          :background="true"
          v-model:current-page="state.newsList.page"
          layout="prev, pager, next"
          :default-page-size="10"
          @current-change="(page: number) => getList(page)"
          :total="state.newsList.totalCount"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.content {
  padding: 0 1rem 0 1rem;
  margin-bottom: 2rem;
}

.totalCount {
  font-size: 0.88rem;
}

.news {
  list-style: none;
  padding: 0;

  li {
    margin-bottom: 2.4rem;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.search-div {
  display: grid;
  justify-content: center;
  grid-template-columns: 200px 0.2fr;
  margin-top: 50px;

  .el-button {
    margin-left: 10px;
  }
}
</style>
