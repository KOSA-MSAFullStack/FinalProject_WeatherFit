
import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: "/",
    redirect: "/login"
  },
  { 
    path: '/login',
    name: 'Login',
    component: () => import("@/views/Login.vue"),
    meta: { title: 'Login Page' },
  },
  { 
    path: '/signup',
    name: 'Signup',
    component: () => import("@/views/Signup.vue"),
    meta: { title: 'Signup Page' },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
