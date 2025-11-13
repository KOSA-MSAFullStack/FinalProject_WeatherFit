<template>
  <div class="min-h-screen flex bg-gray-900">
    <!-- 왼쪽 이미지 영역 -->
    <div class="hidden md:flex w-1/2 bg-cover bg-center" :style="{ backgroundImage: `url(${imageUrl})` }">
      <!-- 필요하면 어두운 오버레이 추가 -->
      <div class="bg-black/30 w-full h-full flex items-center justify-center">
        <h1 class="text-5xl font-bold text-white drop-shadow-lg">WeatherFit</h1>
      </div>
    </div>

    <!-- 오른쪽 로그인 폼 영역 -->
    <div class="w-full md:w-1/2 flex items-center justify-center p-8 bg-linear-to-br from-gray-50 to-gray-100">
      <div class="w-full max-w-md">

        <!-- 로그인 폼 -->
        <div class="space-y-6">
          <h3 class="text-3xl font-medium text-gray-900 text-center">로그인</h3>

          <form @submit.prevent="handleLogin" class="space-y-4">
            <!-- 이메일 입력 -->
            <div class="relative">
              <Mail class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                v-model="email"
                type="email"
                placeholder="example@email.com"
                class="w-full pl-12 pr-4 py-3.5 border border-gray-300 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
                required
              />
            </div>

            <!-- 비밀번호 입력 -->
            <div class="relative">
              <Lock class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <input
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="비밀번호를 입력하세요"
                class="w-full pl-12 pr-12 py-3.5 border border-gray-300 rounded-xl text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
                required
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-300 transition-colors"
              >
                <EyeOff v-if="showPassword" class="w-5 h-5" />
                <Eye v-else class="w-5 h-5" />
              </button>
            </div>

            <div v-if="loginErrorMessage" class="text-sm text-red-500 text-center font-medium">
              {{ loginErrorMessage }}
            </div>

            <!-- 로그인 버튼 -->
            <button
              type="submit"
              class="w-full bg-linear-to-r from-cyan-500 to-blue-500 text-white py-3.5 rounded-xl font-medium hover:from-cyan-600 hover:to-blue-600 transition-all transform hover:scale-[1.02] active:scale-[0.98] shadow-lg"
            >
              로그인
            </button>
          </form>

          <!-- 회원가입 링크 -->
          <div class="text-center text-sm text-gray-400">
            계정이 없으신가요?
            <RouterLink to="/signup" class="text-cyan-400 hover:text-cyan-300 font-medium ml-1">회원가입</RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Mail, Lock, Eye, EyeOff } from 'lucide-vue-next';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/authStore';

const router = useRouter();
const authStore = useAuthStore(); 

const imageUrl = new URL('../assets/login-image.jpg', import.meta.url).href;
const email = ref('');
const password = ref('');
const showPassword = ref(false);
const loginErrorMessage = ref('');

const handleLogin = async () => {
  //console.log('Login attempt:', { email: email.value, password: password.value });
  loginErrorMessage.value = '';
  
  if (!email.value || !password.value) {
    loginErrorMessage.value = '이메일과 비밀번호를 모두 입력해주세요.';
    return;
  }

  try {
    // API 호출, Access Token 저장, axios 헤더 설정을 모두 여기서 처리
    const result = await authStore.login(email.value, password.value); 

    if (result.success) {
      router.push('/main');
    } else {
      loginErrorMessage.value = result.message;
    }
    

  } catch (error) {
    //console.error('로그인 실패:', error);
    let errorMessage = '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.';

    // Axios 에러 처리 (스토어 내부의 axios 요청에서 발생한 에러를 catch)
    if (error.response) {
        if (error.response.status === 401) {
            errorMessage = '이메일 또는 비밀번호가 일치하지 않습니다.';
        } else if (error.response.data && error.response.data.message) {
            errorMessage = error.response.data.message;
        }
    } else if (error.message) {
         // 네트워크 오류 또는 스토어 내에서 발생한 일반 오류 메시지
         errorMessage = error.message;
    }
    
    loginErrorMessage.value = errorMessage;
  }
};

</script>