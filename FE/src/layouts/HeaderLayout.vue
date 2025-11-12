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
          <RouterLink to="/login" aria-label="로그인/로그아웃" class="hover:text-cyan-600 transition-colors">
            <LogOut class="w-6 h-6" />
          </RouterLink>
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
import { RouterLink } from 'vue-router';
import { Heart, UserCircle, ShoppingCart, LogOut, MoveUpRight } from 'lucide-vue-next'; 

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
</script>