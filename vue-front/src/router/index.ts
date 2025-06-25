import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MainView from '@/views/MainView.vue'
import NewsView from '@/views/NewsView.vue'
import SubjectView from '@/views/SubjectView.vue'
import MyPageView from '@/views/MyPageView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/main',
      name: 'main',
      component: MainView,
    },
    {
      path: '/news/:newsId',
      name: 'news',
      component: NewsView,
      props: (route) => ({
        newsId: Number(route.params.newsId),
      }),
    },
    {
      path: '/subjects',
      name: 'subjects',
      component: SubjectView,
      meta: {
        reload: true,
      },
    },
    {
      path: '/mypage',
      name: 'mypage',
      component: MyPageView,
    },
  ],
})

export default router
