<template>
  <header class="sticky top-0 z-40 bg-white shadow-md border-b border-gray-100">
    <div class="px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">

      <div class="flex items-center space-x-6">
        <RouterLink to="/main" class="flex items-center space-x-2 text-gray-900 hover:text-cyan-600 transition-colors">
          <div class="w-6 h-6 bg-cyan-500 rounded-full flex items-center justify-center text-white font-bold text-xs">W</div>
          <div class="text-xl font-extrabold tracking-tight">WeatherFit</div>
        </RouterLink>
        
        <RouterLink to="/store" class="hidden md:flex items-center text-sm font-medium text-gray-500 hover:text-cyan-600 transition-colors">
          Store 바로가기
          <MoveUpRight class="w-4 h-4 ml-1" />
        </RouterLink>
      </div>

      <div class="hidden sm:block">
        <h2 class="text-base font-semibold text-gray-700">
          {{ weatherData.city }} 
          <span class="ml-1 text-cyan-600">{{ weatherData.icon }}</span> 
          <span class="ml-1 font-normal">{{ weatherData.status }}, {{ weatherData.minTemp }}°C / {{ weatherData.maxTemp }}°C</span>
        </h2>
      </div>

      <div class="flex items-center space-x-4">
        
        <div class="flex space-x-4 text-gray-600">
          <RouterLink to="/wishlist" aria-label="찜 목록" class="hover:text-cyan-600 transition-colors">
            <Heart class="w-6 h-6" />
          </RouterLink>
          <RouterLink to="/mypage" aria-label="마이페이지" class="hover:text-cyan-600 transition-colors">
            <UserCircle class="w-6 h-6" />
          </RouterLink>
          <RouterLink to="/cart" aria-label="장바구니" class="hover:text-cyan-600 transition-colors">
            <ShoppingCart class="w-6 h-6" />
          </RouterLink>
          <div 
            aria-label="로그아웃" 
            class="cursor-pointer hover:text-cyan-600 transition-colors"
            @click="handleLogout"
          >
            <LogOut class="w-6 h-6" />
          </div>
        </div>
        
        <div class="hidden lg:flex items-center space-x-2">
          <input
            v-model="cityInput"
            id="city"
            placeholder="도시: Seoul"
            class="p-1.5 border border-gray-300 rounded-lg text-sm w-28 focus:ring-cyan-500 focus:border-cyan-500"
          />
          <button 
            id="btnWeather" 
            class="btn bg-cyan-500 text-white text-sm font-medium px-3 py-1.5 rounded-lg hover:bg-cyan-600 transition-colors"
            @click="handleWeatherUpdate"
          >
            업데이트
          </button>
        </div>

      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue';
import { RouterLink, useRouter } from 'vue-router';
import { Heart, UserCircle, ShoppingCart, LogOut, MoveUpRight } from 'lucide-vue-next'; 
import api from '@/utils/axios';

const router = useRouter();

const weatherData = ref({
  city: '대전',
  icon: '☀️',
  status: '맑음',
  minTemp: 10,
  maxTemp: 17
});

const cityInput = ref('');

const handleWeatherUpdate = () => {
  if (cityInput.value) {
    alert(`도시 '${cityInput.value}'에 대한 날씨 업데이트를 요청합니다.`);
  } else {
    alert('도시를 입력해주세요.');
  }
};

/**
 * 로그아웃 처리 함수
 * 백엔드의 POST /users/logout 엔드포인트에 요청을 보내고, 
 * 서버가 쿠키와 DB의 리프레시 토큰을 무효화하도록 합니다.
 */
const handleLogout = async () => {
  console.log('로그아웃 요청 시작...');
  
  // 백엔드 API 엔드포인트 URL
  const logoutUrl = '/users/logout'; 
  
  try {
    // 데이터는 보낼 필요가 없으므로 {} 사용
    const response = await api.post(logoutUrl, {}, {
      // api 인스턴스에 withCredentials가 기본 설정되어 있다면 생략 가능
      withCredentials: true 
    });

    // 백엔드가 200 OK와 메시지("로그아웃 되었습니다.")를 반환한다고 가정
    if (response.status === 200) {
      console.log("✅ 로그아웃 성공 응답:", response.data);
      alert('로그아웃되었습니다.');
      
      // 로그아웃 성공 시 로그인 페이지로 리디렉션
      router.push('/login'); 
    }
  } catch (error) {
    // 4xx 또는 5xx 오류 처리 (서버에서 쿠키를 지우는 도중 에러가 발생했을 때)
    console.error('로그아웃 실패:', error);
    
    // 로그아웃은 성공적으로 처리되었으나(쿠키가 지워졌으나) 서버에서 오류가 났을 수 있으므로
    // 최악의 경우를 대비하여 로그인 화면으로 이동시키는 것을 고려할 수 있습니다.
    // 여기서는 실패 메시지를 띄우는 것으로 처리합니다.
    let errorMessage = '로그아웃 처리 중 오류가 발생했습니다.';
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message;
    }
    alert(errorMessage);
  }
};
</script>