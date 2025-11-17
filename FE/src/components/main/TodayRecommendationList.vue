<!-- author: 김경아 -->
<template>
  <section
    class="bg-white border border-gray-200"
    style="margin-top: 16px; border-radius: 16px; padding: 14px"
    id="wx"
  >

    <!-- 로딩 상태 -->
    <p v-if="isLoading" style="font-size: 14px; color: #666">추천 상품을 불러오는 중입니다...</p>

    <!-- 에러 상태 -->
    <p v-else-if="isError" style="font-size: 14px; color: #e11d48">
      추천 상품을 불러오지 못했어요. 잠시 후 다시 시도해주세요.
    </p>

    <!-- 데이터 표시 -->
    <div
      v-else
      class="slider"
      style="display: grid; grid-auto-flow: column; grid-auto-columns: minmax(220px, 1fr); gap: 12px; overflow-x: auto; padding-bottom: 6px; scroll-snap-type: x mandatory"
    >
      <MainItemCard
        v-if="todayRecommendation"
        :outer="todayRecommendation.outer"
        :top="todayRecommendation.top"
        :bottom="todayRecommendation.bottom"
      />

    </div>
  </section>
</template>

<script setup>
import { computed, inject } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import MainItemCard from '@/components/main/MainItemCard.vue'
import { getTodayRecommendation } from '@/api/recommendationApi.js'

// App.vue에서 provide한 region 주입
const region = inject('region')

// region 따라 queryKey도 달라지도록
const queryKey = computed(() => ['todayRecommendation', region.value])

// TanStack Query로 오늘 추천 API 호출
const { data, isLoading, isError } = useQuery({
  queryKey,  // 캐시키 : 주소가 바뀌면 다른 캐시로 인식
  queryFn: async () => {
    // 실제 함수 호출: 주소를 넣어서 API 호출
    const res = await getTodayRecommendation(region.value)
    return res;
  },
})

// data.value 전체를 그대로 쓰고, 컴포넌트에 바로 넘겨줌
const todayRecommendation = computed(() => data.value || null)
</script>
