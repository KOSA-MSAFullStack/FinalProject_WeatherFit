<!-- src/components/main/ProductCard.vue -->
<template>
  <div 
    class="flex-shrink-0 bg-white border border-gray-200 overflow-hidden shadow-sm"
    style="scroll-snap-align: start; border-radius: 14px; min-width: 220px; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04)"
  >
    <!-- 이미지 영역 -->
    <div 
      class="bg-gradient-to-b from-gray-100 to-gray-200"
      :style="{
        aspectRatio: '4/3',
        backgroundImage: displayImage ? `url('${displayImage}')` : 'none',
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }"
    />

    <!-- 텍스트 / 버튼 영역 -->
    <div style="padding: 10px">
      <div class="flex items-center justify-between" style="margin-bottom: 8px">
        <div class="truncate" style="max-width: 300px">
          {{ displayName }}
        </div>

        <!-- <span 
          v-if="displayTag"
          class="text-emerald-900 bg-emerald-100"
          style="font-size: 11px; color: #065f46; background: #d1fae5; padding: 3px 8px; border-radius: 999px"
        >
          {{ displayTag }}
        </span> -->
      </div>

      <div class="flex items-center justify-between">
        <button 
          class="bg-black text-white font-bold cursor-pointer"
          style="background: #000000; color: #ffffff; padding: 6px 10px; border-radius: 10px; border: none; font-weight: 700; cursor: pointer"
          @click="onAddToCart"
        >
          장바구니
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

//  부모에서 :item="item" 으로 넘김
const props = defineProps({
  // {
  //   itemId: 4,
  //   itemName: "...",
  //   imageURL: "uploads/~~~.webp"
  // }
  item: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['add-to-cart'])

// 이미지 URL
const displayImage = computed(() => {
  return (
    "http://localhost:8080/" + props.item.imageURL
  )
})

// 상품명
const displayName = computed(() => {
  return props.item.itemName
})

// // 태그(카테고리)
// const displayTag = computed(() => {
//   return (
//     props.item.categoryName ||
//     props.item.category ||
//     props.item.tag ||
//     ''
//   )
// })

// 버튼 클릭 시
const onAddToCart = () => {
  console.log('장바구니에 추가:', displayName.value, props.item)
  emit('add-to-cart', props.item) // 나중에 부모에서 실제 장바구니 처리
}
</script>

<style scoped>
button:hover {
  opacity: 0.9;
}
</style>
