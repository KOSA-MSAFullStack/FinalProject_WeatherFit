<template>
  <header class="sticky top-0 z-40 bg-white shadow-md border-b border-gray-100">
    <div class="px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">

      <div class="flex items-center space-x-6">
        <RouterLink v-if="userRole === 'ROLE_USER'" to="/main" class="flex items-center space-x-2 text-gray-900 hover:text-cyan-600 transition-colors">
          <div class="text-xl font-extrabold tracking-tight">WeatherFit</div>
        </RouterLink>
        <RouterLink v-if="userRole === 'ROLE_ADMIN'" to="/admin/mypage" class="flex items-center space-x-2 text-gray-900 hover:text-cyan-600 transition-colors">
          <div class="text-xl font-extrabold tracking-tight">WeatherFit</div>
        </RouterLink>
        
        <RouterLink to="/store" class="hidden md:flex items-center text-sm font-medium text-gray-500 hover:text-cyan-600 transition-colors">
          Store 바로가기
          <MoveUpRight class="w-4 h-4 ml-1" />
        </RouterLink>
      </div>

      <!-- 가운데: 오늘 날씨 표시 -->
      <div v-if="userRole === 'ROLE_USER'" class="hidden sm:block">
        <h2 class="text-base font-semibold text-gray-700">
          <!-- 로딩 상태 -->
          <span v-if="isLoading">날씨 정보를 불러오는 중입니다...</span>

          <!-- 에러 상태 (HTTP 에러, 네트워크 에러 등) -->
          <span v-else-if="isError" class="text-red-500">올바른 지역이 아니거나, 해당 지역의 날씨 정보를 찾을 수 없습니다.</span>

          <!-- 정상적으로 날씨가 있는 경우만 기존 UI 렌더링 -->
          <span v-else class="font-normal font-semibold">
            {{ region }} |
              <img 
                v-if="iconUrl" 
                :src="iconUrl" 
                alt="날씨 아이콘" 
                class="inline-block w-6 h-6"
              />
            {{ weatherData.condition }} |
            최저기온 : {{ weatherData.minTemperature }}°C / 최고기온 : {{ weatherData.maxTemperature }}°C
          </span>
        </h2>
      </div>

      <div class="flex items-center space-x-4">
        
        <div class="flex space-x-4 text-gray-600">
          <!-- <RouterLink v-if="userRole === 'ROLE_USER'" to="/wishlist" aria-label="찜 목록" class="hover:text-cyan-600 transition-colors">
            <Heart class="w-6 h-6" />
          </RouterLink> -->
          <RouterLink v-if="userRole === 'ROLE_USER'" to="/mypage" aria-label="마이페이지" class="hover:text-cyan-600 transition-colors">
            <UserCircle class="w-6 h-6" />
          </RouterLink>
          <RouterLink v-if="userRole === 'ROLE_USER'" to="/cart" aria-label="장바구니" class="hover:text-cyan-600 transition-colors">
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
        
        <div v-if="userRole === 'ROLE_USER'" class="hidden lg:flex items-center space-x-2">
          <input
            v-model="cityInput"
            id="city"
            placeholder="예시: 대전시 도룡동"
            class="p-1.5 border border-gray-300 rounded-lg text-sm w-40 focus:ring-cyan-500 focus:border-cyan-500"
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
import { computed, ref } from 'vue';
import { inject } from 'vue'
import { RouterLink, useRouter } from 'vue-router';
import { Heart, UserCircle, ShoppingCart, LogOut, MoveUpRight } from 'lucide-vue-next';
import { useAuthStore } from '@/store/authStore'
import { getTodayWeather } from '@/api/weatherApi.js'
import { useQuery } from '@tanstack/vue-query';

const router = useRouter();
const authStore = useAuthStore();

const userRole = computed(() => authStore.user.role);
console.log(userRole.value)

// 날씨 정보
const region = inject('region')  // App.vue에서 받은 전역 상태 (ref)
const cityInput = ref(''); // NavBar에서만 쓰는 로컬 입력 상태

// TanStack Query로 오늘 날씨 조회
const { data, isLoading, isError } = useQuery({
  // 주소가 바뀔 때마다 다른 캐시/요청이 되도록 queryKey에 region 반영
  queryKey: computed(() => ['todayWeather', region.value]),
  
  // 실제 API 호출
  queryFn: async () => {
    const res = await getTodayWeather(region.value)
    return res
  },
})

// 템플릿에서 쓰기 편하게 가공
const weatherData = computed(() => {
  // 아직 데이터가 없을 때 기본값
  if (!data.value) {
    return {
      condition: '',
      minTemperature: '-',
      maxTemperature: '-',
      icon: '',
    }
  }
  // 백엔드에서 내려주는 today weather DTO 구조에 맞게 사용
  // 예: { status, minTemp, maxTemp, icon }
  return data.value
})

// 아이콘 url
const iconUrl = computed(() => {
  const iconCode = weatherData.value.icon  // 여기서 .value 필수!
  if (!iconCode) return ''                 // 없으면 빈 문자열

  return `https://openweathermap.org/img/wn/${iconCode}.png`
})


const handleWeatherUpdate = () => {
  const trimmed = cityInput.value.trim()

  if (!trimmed) {
    alert('도시를 입력해주세요.')
    return // ❗ 여기서 함수 종료 → region 안 바뀜 → API 안 호출됨
  }

  // 주소값 입력 시에만 전역 region 변경
  region.value = trimmed
};

/**
 * 로그아웃 처리 함수
 * 이제 이 함수는 auth 스토어의 logout 액션을 호출하는 역할만 합니다.
 */
const handleLogout = async () => {  
  // 스토어의 logout 액션을 호출하고, router 인스턴스를 전달합니다.
  await authStore.logout(router);
};
</script>