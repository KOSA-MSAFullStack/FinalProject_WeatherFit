
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/authStore';

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
      { 
        path: 'admin/mypage',
        name: 'AdminMyPage',
        component: () => import("@/views/AdminMyPage.vue"),
        meta: { title: '관리자 페이지' },
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
        meta: { 
          title: '메인 페이지',
          requiresAuth: true // 로그인이 필요한 페이지에 메타 필드 추가
        },
      },
      {
        path: 'mypage', // '/mypage' 경로
        name: 'MyPage',
        component: () => import("@/views/MyPage.vue"),
        meta: { 
          title: '내 정보 페이지',
          requiresAuth: true // 로그인이 필요한 페이지에 메타 필드 추가
        },
      },
      {
        path: 'items/:itemId',
        name: 'ItemDetail',
        component: () => import('@/views/ItemDetail.vue'),
        meta: { 
          title: '상품 상세 조회 페이지',
          requiresAuth: true // 로그인이 필요한 페이지에 메타 필드 추가
        },
      },
      {
        path: 'cart', // '/cart' 경로
        name: 'Cart',
        component: () => import("@/views/Cart.vue"),
        meta: { 
          title: '장바구니 페이지',
          requiresAuth: true // 로그인이 필요한 페이지에 메타 필드 추가
        },
      },
      {
        path: 'store', // '/store' 경로
        name: 'Store',
        component: () => import("@/views/Store.vue"),
        meta: { 
          title: '스토어 페이지',
          requiresAuth: true // 로그인이 필요한 페이지에 메타 필드 추가
        },
      },
      // 예: 장바구니 등 다른 경로에도 필요 시 추가
      // { path: 'mypage', name: 'MyPage', component: MyPageComp, meta: { requiresAuth: true } },
    ]
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 전역 네비게이션 가드 설정
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  // 아직 인증 초기화(새로고침 시 토큰 재발급 시도)가 안됐다면, 여기서 완료될 때까지 기다립니다.
  if (!authStore.isAuthReady) {
    await authStore.initializeAuth();
  }
  
  // 라우트 메타 필드를 확인하여 인증이 필요한 페이지인지 확인합니다.
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  
  // 로그인이 필요한 페이지인데, 로그인이 되어있지 않다면 로그인 페이지로 보냅니다.
  if (requiresAuth && !authStore.isAuthenticated) {
    console.log('인증이 필요하여 로그인 페이지로 이동합니다.');
    next({ name: 'Login' });
  } else {
    // 그 외의 모든 경우는 정상적으로 페이지 이동을 허용합니다.
    next();
  }
});

export default router;
