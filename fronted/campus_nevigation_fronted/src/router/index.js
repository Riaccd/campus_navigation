import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'  // 更新导入路径

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router