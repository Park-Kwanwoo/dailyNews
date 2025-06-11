<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { container } from 'tsyringe'
import NewsRepository from '@/repository/NewsRepository.ts'
import { useAuthStore } from '@/store/useAuthStore.ts'
import NewsItems from '@/entity/NewsItems.ts'
import NewsResponse from '@/entity/NewsResponse.ts'
import { Document } from '@element-plus/icons-vue'

const props = defineProps({
  newsId: Number,
})

type StateType = {
  newsItems: NewsItems
  news: NewsResponse
}

const state = reactive<StateType>({
  newsItems: new NewsItems(),
  news: new NewsResponse(),
})

const authStore = useAuthStore()
const NEWS_REPOSITORY = container.resolve(NewsRepository)

onMounted(() => {
  NEWS_REPOSITORY.get(props.newsId, authStore.accessToken).then((newsResponse) => {
    state.news = newsResponse
  })
})
</script>

<template>
  <div class="news-detail-container">
    <el-card class="box-card" shadow="always">
      <div class="topic-header">
        <el-icon><Document /></el-icon>
        <span class="topic-text">“{{ state.news.title }}” 관련 주요 뉴스</span>
      </div>
    </el-card>

    <el-divider content-position="left">뉴스 목록</el-divider>

    <el-row :gutter="20">
      <el-col v-for="(item, index) in state.news.items" :key="index" :span="24" class="news-card">
        <el-card shadow="hover">
          <template #header>
            <span class="news-headline">{{ item.headline }}</span>
          </template>

          <div class="news-summary">{{ item.summary }}</div>

          <el-divider border-style="dashed" />

          <div class="news-source">
            출처:
            <el-link
              :href="item.sourceUrl"
              target="_blank"
              type="primary"
              :underline="false"
              rel="noopener norefrrer"
            >
              {{ item.source }}
            </el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.news-detail-container {
  padding: 20px;
}

.news-card {
  margin-bottom: 20px;
}

.news-headline {
  font-weight: bold;
  font-size: 18px;
}

.news-summary {
  margin-top: 10px;
  font-size: 15px;
  line-height: 1.6;
}

.news-source {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}
</style>
