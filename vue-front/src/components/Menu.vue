<script setup lang="ts">
import { useAuthStore } from '@/store/useAuthStore.ts'
import { computed } from 'vue'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn())

function logout() {
  authStore.logout()
}
</script>

<template>
  <ul class="menus">
    <li class="menu">
      <router-link to="/" v-if="isLoggedIn" @click="logout">로그아웃</router-link>
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

  a {
    color: inherit;
  }
}
</style>
