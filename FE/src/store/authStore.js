import { defineStore } from 'pinia';
import axios from '@/utils/axios'; // 기존 axios 인스턴스 사용

// Pinia 스토어 정의
export const useAuthStore = defineStore('auth', {
    // 상태 (State): 인메모리 데이터 저장
    state: () => ({
        accessToken: null, // Access Token (인메모리)
        isAuthenticated: false, // 인증 여부
        isAuthReady: false, // 앱 초기화 준비 완료 여부 (새로고침 후 AT 복구 시도 완료)
        isRefreshing: false, // 토큰 재발급 로직 중복 실행 방지 플래그
        loginError: null, // 로그인 오류 메시지 저장
    }),

    // 게터 (Getters): 계산된 속성
    getters: {
        getAccessToken: (state) => state.accessToken,
        getIsAuthenticated: (state) => state.isAuthenticated,
        getIsAuthReady: (state) => state.isAuthReady,
        getLoginError: (state) => state.loginError,
    },

    // 액션 (Actions): 상태 변경 및 비동기 로직
    actions: {
        // --- 헬퍼: Access Token 설정 및 상태 업데이트 ---
        setAccessToken(token) {
            this.accessToken = token;
            this.isAuthenticated = !!token;
            this.loginError = null; // 토큰 설정 시 오류 메시지 초기화
            
            console.log(`[AuthStore] Access Token 설정 완료. 인증 상태: ${this.isAuthenticated}`);
        },

        // --- 초기화: 앱 시작 시 AT 복구 시도 ---
        async initializeAuth() {
            if(this.isAuthReady) return;

            if (this.accessToken) {
                this.isAuthReady = true;
                return; 
            }
            
            console.log("인메모리 AT 없음. Refresh Token으로 AT 복구 시도 중...");
            const success = await this.refreshAccessToken();
            
            this.isAuthReady = true;
            console.log(`[AuthStore] 인증 초기화 완료. Ready: ${this.isAuthReady}, Success: ${success}`);
        },
        
        // --- 로그인: AT/RT 획득 ---
        async login(email, password) {
            this.loginError = null; // 시도 전 오류 초기화
            try {
                const response = await axios.post('/users/login', { email, password }); 
                
                const accessToken = response.data.accessToken;
                this.setAccessToken(accessToken);
                const role = response.data.role;
                return { success: true, message: '로그인 성공!', role };

            } catch (error) {
                let errorMessage = "알 수 없는 오류로 로그인에 실패했습니다.";

                if (error.response) {
                    if (error.response.data && error.response.data.message) {
                        errorMessage = error.response.data.message;
                    } 
                    else if (error.response.status === 401) {
                        errorMessage = "이메일 또는 비밀번호가 일치하지 않습니다.";
                    }
                    else if (error.response.status === 500) {
                        errorMessage = "로그인에 실패했습니다. 서버 오류가 발생했습니다 (500).";
                    }
                    else {
                        errorMessage = `로그인에 실패했습니다. 오류 코드: ${error.response.status}`;
                    }
                } else if (error.request) {
                    errorMessage = "서버에 연결할 수 없습니다. 네트워크 상태를 확인해주세요.";
                }
                
                this.loginError = errorMessage; 
                this.setAccessToken(null);
                return { success: false, message: errorMessage };
            }
        },
        
        // --- 로그아웃: AT 삭제, RT 무효화, 페이지 이동까지 담당 ---
        async logout(router) { // router 인스턴스를 인자로 받도록 수정
            try {
                await axios.post('/users/logout');
            } catch (error) {
                // 401 에러는 토큰이 이미 만료된 것이므로 무시하고 로그아웃 처리 계속
                if(error.response?.status !== 401) {
                    console.warn('백엔드 로그아웃 처리 실패:', error);
                    // 여기서 사용자에게 "로그아웃에 실패했습니다" 같은 알림을 띄울 수도 있습니다.
                }
            } finally {
                // API 성공/실패 여부와 관계없이 클라이언트의 상태를 확실히 초기화합니다.
                this.setAccessToken(null);

                // 로그아웃 후 로그인 페이지로 이동합니다.
                // 현재 페이지가 이미 /login이 아닐 경우에만 이동하도록 방어 코드를 추가합니다.
                if (router && router.currentRoute.value.path !== '/login') {
                    console.log('로그아웃 완료. 로그인 페이지로 이동합니다.');
                    router.push('/login');
                }
            }
        },

        // --- 토큰 재발급: 새로고침 후 AT 복구 및 401 에러 핸들링 ---
        async refreshAccessToken() {
            if (this.isRefreshing) {
                // 이미 재발급 시도 중이면 대기
                return new Promise(resolve => {
                    const check = setInterval(() => {
                        if (!this.isRefreshing) {
                            clearInterval(check);
                            // Promise가 종료된 시점의 최종 인증 상태를 반환
                            resolve(this.isAuthenticated); 
                        }
                    }, 50);
                });
            }
            
            this.isRefreshing = true;
            try {
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

// --- axios 인터셉터 설정 ---
let isInterceptorsSetup = false;

if (!isInterceptorsSetup) {
    // 요청 인터셉터: 요청이 나가기 전에 AT를 Authorization 헤더에 추가 (Refresh 요청 제외)
    axios.interceptors.request.use(config => {
        const store = useAuthStore();
        const isAuthRequest = config.url === '/users/login' || config.url === '/users/refresh';
        
        if (!isAuthRequest && store.getAccessToken && !config.headers['Authorization']) {
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
            
            // 로그인, 로그아웃, 재발급 요청은 401 처리 로직을 건너뛴다.
            const isAuthAttempt = originalRequest.url === '/users/login' 
                               || originalRequest.url === '/users/logout'
                               || originalRequest.url === '/users/refresh';

            // 1. 401 에러이고, 2. 인증 요청이 아니며, 3. 재시도 플래그가 없는 경우에만 재발급 로직 실행
            if (error.response?.status === 401 
                && !isAuthAttempt
                && !originalRequest._retry) {
                
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
                    // refreshAccessToken 내부에서 실패 시 setAccessToken(null)이 호출됨
                    // store.logout(); // 이전에 실패했기 때문에 추가적인 강제 로그아웃은 불필요할 수 있음
                    
                    // --- 실제 Vue Router 사용 시:
                    // router.push('/login'); 
                    // ---
                }
            }
            
            // 재발급 실패, 또는 401이 아닌 다른 에러는 Promise.reject로 반환
            return Promise.reject(error);
        }
    );
    isInterceptorsSetup = true;
}

// Interceptor 설정은 스토어 파일이 로드될 때 한 번만 실행됩니다.