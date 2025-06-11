<script setup lang="ts">
import { useAuthStore } from '@/store/useAuthStore.ts'
import { computed } from 'vue'
import { SOCIAL_AUTH_REPOSITORY } from '@/repository/httpProviders.ts'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn())

function logout() {
  SOCIAL_AUTH_REPOSITORY.logout()
}
</script>

<template>
  <ul class="menus">
    <li class="menu">
      <router-link to="/subjects" v-if="isLoggedIn">주제 변경</router-link>
    </li>
    <li class="menu">
      <el-button to="/" v-if="isLoggedIn" @click="logout">로그아웃</el-button>
    </li>
  </ul>
</template>
<style scoped lang="scss">
.menus {
  height: 20px;
  list-style: none;
  padding: 0;
  font-size: 0.88rem;
  font-weight: 300;
  text-align: center;
  margin: 0;
}

.menu {
  display: inline;
  margin-right: 1rem;

  &:last-child {
    margin-right: 0;
  }

  a,
  button {
    color: inherit;
    background: none;
    border: none;
    padding: 0;
    font: inherit;
    cursor: pointer;
    text-decoration: underline;
  }

  button:focus {
    outline: none;
  }
}
</style>
