<!-- src/components/main/TodayRecommendationList.vue -->
<template>
  <section
    class="bg-white border border-gray-200"
    style="margin-top: 16px; border-radius: 16px; padding: 14px"
    id="wx"
  >
    <div class="flex items-center justify-between" style="margin-bottom: 8px">
      <h2 class="text-lg font-semibold text-gray-900" style="margin: 0">이번주 날씨에 맞는 추천 상품</h2>
    </div>

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
    <ProductCard
    v-for="item in items"
    :key="item.itemId"
    :item="item"
    />

    </div>
  </section>
</template>

<script setup>
import { computed, inject } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import ProductCard from '@/components/main/ProductCard.vue'
import { getWeeklyRecommendation } from '@/api/recommendationApi.js'

// App.vue에서 provide한 region 주입
const region = inject('region')

// region 따라 queryKey도 달라지도록
const queryKey = computed(() => ['weeklyRecommendation', region.value])

// TanStack Query로 오늘 추천 API 호출
const { data, isLoading, isError } = useQuery({
  queryKey,  // 캐시키 : 주소가 바뀌면 다른 캐시로 인식
  queryFn: async () => {
    // 실제 함수 호출: 주소를 넣어서 API 호출
    const res = await getWeeklyRecommendation(region.value)
    return res;
  },
})

// data.value를 배열로 만들어서 컴포넌트에 넘겨줌
const items = computed(() => {
  if (!data.value) return []

  const { firstItem, secondItem, thirdItem } = data.value
  const list = [firstItem, secondItem, thirdItem].filter(Boolean)

  return list
})
</script>
