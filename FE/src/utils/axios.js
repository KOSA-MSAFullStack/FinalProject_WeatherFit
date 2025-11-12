import axios from 'axios';

// 기본 인스턴스 설정
const api = axios.create({
  // 백엔드 서버의 기본 URL을 설정
  baseURL: 'http://localhost:8080',
  timeout: 5000, // 5초 타임아웃
  // HttpOnly 쿠키 전송을 위해 필수
  withCredentials: true, 
});

// 응답 인터셉터 설정 (에러 처리 및 로그아웃 유도)
api.interceptors.response.use(
  (response) => {
    // 2xx 범위의 상태 코드는 이 함수를 트리거합니다.
    return response;
  },
  (error) => {
    // 2xx 외의 상태 코드는 이 함수를 트리거합니다.
    const { response } = error;
    
    // 401 Unauthorized 처리: Access Token이 만료되거나 유효하지 않은 경우
    if (response && response.status === 401) {
      // TODO: 여기에 Refresh Token 재발급 로직을 구현하거나, 
      // 사용자에게 로그아웃을 유도하고 로그인 페이지로 리다이렉트하는 로직을 추가해야 합니다.
      console.error('인증 실패 (401): 토큰이 만료되었거나 유효하지 않습니다.');
    }

    return Promise.reject(error);
  }
);

export default api;