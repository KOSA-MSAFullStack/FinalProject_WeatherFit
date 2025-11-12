import { createRouter, createWebHistory } from "vue-router";
import AdminMyPage from "../views/AdminMyPage.vue";

const routes = [
  {
    path: "/login",
    name: "Login",
    //component: () => import("@/views/Login.vue"),
    meta: { title: "Login Page" },
  },
  {
    path: "/signup",
    name: "Signup",
    //component: () => import("@/views/Signup.vue"),
    meta: { title: "Signup Page" },
  },
  {
    path: "/admin/mypage",
    name: "AdminMyPage",
    component: AdminMyPage,
    //path: "/",
    //redirect: "/login",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
