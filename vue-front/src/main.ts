import 'reflect-metadata'

import { createApp } from 'vue'

import App from './App.vue'
import router from './router'

// Element-plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Bootstrap
import 'bootstrap/dist/css/bootstrap-utilities.css'

// normalize
import 'normalize.css'
import { createPinia } from 'pinia'

const app = createApp(App)
const pinia = createPinia()

app.use(ElementPlus)
app.use(router)
app.use(pinia)

app.mount('#app')
