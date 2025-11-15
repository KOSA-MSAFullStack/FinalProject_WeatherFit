<!-- author: 김경아 -->
<template>
  <div style="font-family: 'Noto Sans KR', system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif">
    <!-- Header -->
    <header 
      class="sticky top-0 z-20 border-b border-gray-200"
      style="backdrop-filter: blur(8px); background: rgba(255, 255, 255, 0.8)"
    >
    </header>

    <!-- Main Content -->
    <main style="margin: 0 auto; padding: 16px; padding-bottom: 40px">
      <!--오늘 날씨 기반 옷 추천-->
      <TodayRecommendationList :region="selectedRegion" /> 

      <!-- 내일 날씨 기반 옷 추천-->  
      <TomorrowRecommendationList />  

      <!-- 이번 주 날씨 기반 옷 추천-->
      <WeeklyRecommendationList />    
    </main>

    <!-- Footer -->
    <footer 
      class="text-gray-500 border-t border-gray-200"
      style="font-size: 13px; padding: 24px 0; margin-top: 16px"
    >
      <div style="margin: 0 auto; padding: 0 16px">
        © WeatherFit – 날씨 기반 옷 추천 메인 홈
      </div>
    </footer>
  </div>
</template>

<script setup>
import TodayRecommendationList from '@/components/main/TodayRecommendationList.vue'
import TomorrowRecommendationList from '@/components/main/TomorrowRecommendationList.vue'
import WeeklyRecommendationList from '@/components/main/WeeklyRecommendationList.vue'
import { ref } from 'vue'
const selectedRegion = ref('대전시') // 지역 입력안했을 때 기본값
</script>

<style scoped>
@media (max-width: 1000px) {
  .hero-grid {
    grid-template-columns: 1fr !important;
  }
}
</style>