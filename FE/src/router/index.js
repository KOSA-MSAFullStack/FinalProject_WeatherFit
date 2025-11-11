
import { createRouter, createWebHistory } from 'vue-router';
import AdminMyPage from '../views/AdminMyPage.vue';

const routes = [
  {
    path: '/admin/mypage',
    name: 'AdminMyPage',
    component: AdminMyPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
