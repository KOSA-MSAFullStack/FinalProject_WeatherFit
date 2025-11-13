import axios from 'axios';
import router from '@/router';
import Cookies from 'js-cookie';

// 기본 인스턴스 설정
const api = axios.create({
  // 백엔드 서버의 기본 URL을 설정
  baseURL: 'http://localhost:8080',
  timeout: 60000, // 30초 타임아웃
  // HttpOnly 쿠키 전송을 위해 필수
  withCredentials: true, 
});

// Request interceptor: 요청 보내기 전에 항상 실행
api.interceptors.request.use(config => {
  // 1. 쿠키에서 accessToken을 읽어옵니다.
  const token = Cookies.get('accessToken'); 
  
  // 2. 토큰이 존재하면 Authorization 헤더에 담아줍니다.
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
}, error => {
  return Promise.reject(error);
});

// 응답 인터셉터 설정 (에러 처리 및 로그아웃 유도)
api.interceptors.response.use(
  (response) => {
    // 2xx 범위의 상태 코드는 이 함수를 트리거합니다.
    return response;
  },
  (error) => {
    // HTTP 응답 상태 코드가 있을 때만 처리
    if (error.response) {
      const status = error.response.status;

      // 401 Unauthorized 처리
      if (status === 401) {
          console.error("401 Unauthorized. 인증이 만료되었거나 유효하지 않습니다.");
          alert('인증 정보가 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.');

          // 로그인 페이지로 강제 리다이렉트
          // 현재 경로가 이미 로그인 페이지(/login)라면 리다이렉션 방지
          if (router.currentRoute.value.path !== '/login') {
              router.push('/login');
          }
      } 
      // 403 Forbidden 등 다른 에러 코드는 여기서 추가 처리 가능
      else if (status === 403) {
          alert('접근 권한이 없습니다.');
      }
    }

    return Promise.reject(error);
  }
);

export default api;