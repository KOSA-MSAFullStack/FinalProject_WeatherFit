<!-- author: 김경아 -->
<template>
  <section
    class="bg-white border border-gray-200"
    style="margin-top: 16px; border-radius: 16px; padding: 14px"
    id="wx"
  >
    <div class="flex items-center justify-between" style="margin-bottom: 8px">
      <h1 class="text-2xl font-bold text-gray-900" style="margin-bottom: 10px">내일의 날씨에 맞는 추천 상품</h1>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="slider">
      <div
        v-for="n in SKELETON_COUNT"
        :key="n"
        class="skeleton skeleton-item"
      />
    </div>

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
    <ItemCard
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
import ItemCard from '@/components/main/ItemCard.vue'
import { getTomorrowRecommendation } from '@/api/recommendationApi.js'

// App.vue에서 provide한 region 주입
const region = inject('region')

// region 따라 queryKey도 달라지도록
const queryKey = computed(() => ['tomorrowRecommendation', region.value])

// TanStack Query로 오늘 추천 API 호출
const { data, isLoading, isError } = useQuery({
  queryKey,  // 캐시키 : 주소가 바뀌면 다른 캐시로 인식
  queryFn: async () => {
    // 실제 함수 호출: 주소를 넣어서 API 호출
    const res = await getTomorrowRecommendation(region.value)
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

// 내일 추천은 최대 3개 뿌리니까 3개 스켈레톤
const SKELETON_COUNT = 3
</script>
<style scoped>
/* 로딩/정상 공통 slider */
.slider {
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(220px, 1fr);
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 6px;
  scroll-snap-type: x mandatory;
}

/* ItemCard와 동일하게 4/3 ratio */
.skeleton-item {
  width: 100%;
  aspect-ratio: 4 / 3;
  border-radius: 12px;
  scroll-snap-align: start;
}

/* 스켈레톤 효과 */
.skeleton {
  background: linear-gradient(
    90deg,
    #f3f4f6 25%,
    #e5e7eb 37%,
    #f3f4f6 63%
  );
  background-size: 400% 100%;
  animation: shimmer 1.4s ease infinite;
}

@keyframes shimmer {
  0% { background-position: 100% 0; }
  100% { background-position: -100% 0; }
}

.error {
  font-size: 14px;
  color: #e11d48;
}
</style>