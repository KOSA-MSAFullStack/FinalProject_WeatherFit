import axios from 'axios';

// 기본 인스턴스 설정
const api = axios.create({
  // 백엔드 서버의 기본 URL을 설정
  baseURL: 'http://localhost:8080',
  timeout: 5000, // 5초 타임아웃
  // HttpOnly 쿠키 전송을 위해 필수
  withCredentials: true, 
});

export default api;