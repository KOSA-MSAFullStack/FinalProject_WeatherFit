// store/authStore.js: Pinia를 사용한 인메모리 인증 상태 관리
import { defineStore } from 'pinia';
import axios from '@/utils/axios'; // 기존 axios 인스턴스 사용

// Pinia 스토어 정의
export const useAuthStore = defineStore('auth', {
    // 상태 (State): 인메모리 데이터 저장
    state: () => ({
        accessToken: null, // Access Token (인메모리)
        isAuthenticated: false, // 인증 여부
        isAuthReady: false, // 앱 초기화 준비 완료 여부 (새로고침 후 AT 복구 시도 완료)
        // 토큰 재발급 로직 중복 실행 방지 플래그
        isRefreshing: false, 
    }),

    // 게터 (Getters): 계산된 속성
    getters: {
        getAccessToken: (state) => state.accessToken,
        getIsAuthenticated: (state) => state.isAuthenticated,
        getIsAuthReady: (state) => state.isAuthReady,
    },

    // 액션 (Actions): 상태 변경 및 비동기 로직
    actions: {
        // --- 헬퍼: Access Token 설정 및 상태 업데이트 ---
        setAccessToken(token) {
            this.accessToken = token;
            this.isAuthenticated = !!token;
            
            // axios 기본 헤더 설정 (모든 요청에 AT 자동 포함)
            if (token) {
                axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            } else {
                delete axios.defaults.headers.common['Authorization'];
            }
            
            console.log(`[AuthStore] Access Token 설정 완료. 인증 상태: ${this.isAuthenticated}`);
        },

        // --- 초기화: 앱 시작 시 AT 복구 시도 ---
        async initializeAuth() {
            if (this.accessToken) {
                this.isAuthReady = true;
                return; // 이미 토큰이 메모리에 있다면 재시도 불필요 (SPA 라우팅 시)
            }
            
            console.log("인메모리 AT 없음. Refresh Token으로 AT 복구 시도 중...");
            const success = await this.refreshAccessToken();
            
            // 재발급 성공 여부에 관계없이 인증 초기화는 완료됨
            this.isAuthReady = true;
            console.log(`[AuthStore] 인증 초기화 완료. Ready: ${this.isAuthReady}, Success: ${success}`);
        },
        
        // --- 로그인: AT/RT 획득 ---
        async login(email, password) {
            try {
                // 백엔드 로그인 API 호출
                const response = await axios.post('/users/login', { email, password }); 
                
                const accessToken = response.data.accessToken;

                if (accessToken) {
                    this.setAccessToken(accessToken); // 인메모리 저장 및 axios 헤더 설정
                    return { success: true, message: '로그인 성공!' };
                } else {
                     throw new Error("서버 응답에 Access Token이 없습니다.");
                }

            } catch (error) {
                console.error('로그인 실패:', error);
                // 에러 메시지 반환
                throw error; 
            }
        },
        
        // --- 로그아웃: AT 삭제 및 RT 무효화 ---
        async logout() {
            // 인메모리 AT 삭제
            this.setAccessToken(null);
            
            // 백엔드 로그아웃 API 호출 (HttpOnly RT 쿠키 제거 및 DB 무효화)
            try {
                // 이 요청은 AT가 필요하므로, 로그아웃 전에 현재 AT로 요청을 시도합니다.
                // AT가 만료되어도 RT 제거가 중요하므로 실패를 무시할 수 있습니다.
                await axios.post('/users/logout');
            } catch (error) {
                 console.warn('백엔드 로그아웃 처리 실패 (RT 이미 만료 가능성):', error);
            }
            
            // 기타 정리 작업 (예: 사용자 데이터 초기화)
        },

        // --- 토큰 재발급: 새로고침 후 AT 복구 및 401 에러 핸들링 ---
        async refreshAccessToken() {
            if (this.isRefreshing) {
                // 이미 재발급 시도 중이면 대기
                return new Promise(resolve => {
                    const check = setInterval(() => {
                        if (!this.isRefreshing) {
                            clearInterval(check);
                            resolve(this.isAuthenticated);
                        }
                    }, 50);
                });
            }
            
            this.isRefreshing = true;
            try {
                // /refresh 엔드포인트는 HttpOnly 쿠키의 RT를 자동으로 사용합니다.
                const response = await axios.post('/users/refresh'); 
                
                const newAccessToken = response.data.accessToken;

                if (newAccessToken) {
                    this.setAccessToken(newAccessToken);
                    this.isRefreshing = false;
                    return true; // 성공
                } else {
                    throw new Error("재발급 응답에 새로운 Access Token이 없습니다.");
                }
            } catch (error) {
                console.error('토큰 재발급 실패: 재인증 필요', error);
                this.isRefreshing = false;
                this.setAccessToken(null); // RT도 무효화된 것으로 간주, 인증 상태 해제
                return false; // 실패
            }
        },
    },
});

// --- axios 인터셉터 설정 (가장 중요) ---
// 이 로직은 AuthStore.js가 로드될 때 한 번만 실행되어야 합니다.
let isInterceptorsSetup = false;

if (!isInterceptorsSetup) {
    // 요청 인터셉터: 요청이 나가기 전에 AT가 있는지 확인 (선택 사항)
    axios.interceptors.request.use(config => {
        const store = useAuthStore();
        if (config.url !== '/users/login' && config.url !== '/users/refresh' && store.getAccessToken && !config.headers['Authorization']) {
            config.headers['Authorization'] = `Bearer ${store.getAccessToken}`;
        }
        return config;
    }, error => {
        return Promise.reject(error);
    });

    // 응답 인터셉터: 401 에러를 감지하여 AT 자동 재발급 로직 실행 (핵심 로직)
    axios.interceptors.response.use(
        response => response,
        async (error) => {
            const originalRequest = error.config;
            const store = useAuthStore();
            
            // 1. 401 에러이고, 2. 토큰 재발급 요청이 아니며, 3. 재시도 플래그가 없는 경우
            if (error.response?.status === 401 && originalRequest.url !== '/users/refresh' && !originalRequest._retry) {
                
                originalRequest._retry = true; // 재시도 플래그 설정
                console.log("401 감지: Access Token 재발급 시도...");
                
                try {
                    // 재발급 성공 시
                    const success = await store.refreshAccessToken(); 
                    if (success) {
                        // 새로운 Access Token으로 헤더 업데이트
                        originalRequest.headers['Authorization'] = `Bearer ${store.getAccessToken}`;
                        // 원래 요청을 다시 시도
                        return axios(originalRequest); 
                    }
                } catch (e) {
                    // 재발급 실패 (Refresh Token 만료 등)
                    store.logout(); // 강제 로그아웃
                    // 사용자에게 로그인 페이지로 이동하도록 안내
                    // ** 실제 환경에서는 Vue Router를 사용하여 로그인 페이지로 리다이렉트 필요
                    // 예: router.push('/login'); 
                    return Promise.reject(error);
                }
            }
            
            return Promise.reject(error);
        }
    );
    isInterceptorsSetup = true;
}