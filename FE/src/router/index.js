
import { createRouter, createWebHistory } from 'vue-router';

import DefaultLayout from '@/layouts/DefaultLayout.vue'; 
import AuthLayout from '@/layouts/AuthLayout.vue';

const routes = [
  {
    path: "/",
    component: AuthLayout,
    children: [
      {
        path: '',
        redirect: '/login',
      },
      { 
        path: 'login',
        name: 'Login',
        component: () => import("@/views/Login.vue"),
        meta: { title: '로그인' },
      },
      { 
        path: 'signup',
        name: 'Signup',
        component: () => import("@/views/Signup.vue"),
        meta: { title: '회원가입' },
      },
    ]
  },
  {
    path: '/',
    component: DefaultLayout,
    children: [
      {
        path: 'main', // '/main' 경로
        name: 'Main',
        component: () => import("@/views/Main.vue"),
        meta: { title: '메인 페이지' },
      },
      // 향후 추가될 마이페이지, 장바구니 등 다른 경로들을 여기에 추가
      // { path: 'mypage', name: 'MyPage', component: MyPageComp, meta: { requiresAuth: true } },
    ]
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
