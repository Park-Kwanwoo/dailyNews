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

const app = createApp(App)

app.use(ElementPlus)
app.use(router)

app.mount('#app')
