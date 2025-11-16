<!-- author: 김경아 -->
<template>
  <div 
    class="flex-shrink-0 bg-white border border-gray-200 overflow-hidden shadow-sm"
    style="scroll-snap-align: start; border-radius: 14px; min-width: 220px; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04)"
  >
    <!-- 이미지 영역 -->
    <div 
      class="bg-gradient-to-b from-gray-100 to-gray-200 cursor-pointer"
      :style="{
        aspectRatio: '4/3',
        backgroundImage: displayImage ? `url('${displayImage}')` : 'none',
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }"
      @click="clickItemDetail(item)"
    />

    <!-- 텍스트 / 버튼 영역 -->
    <div style="padding: 10px">
      <div class="flex items-center justify-between" style="margin-bottom: 8px">
        <div class="truncate" @click="clickItemDetail(item)">
          {{ displayName }}
        </div>

        <span 
          v-if="displayTag"
          class="text-emerald-900 bg-emerald-100"
          style="font-size: 11px; color: #065f46; background: #d1fae5; padding: 3px 8px; border-radius: 999px"
        >
          {{ displayTag }}
        </span>
      </div>

      <div class="flex items-center justify-end">
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
import { useRouter } from 'vue-router'

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

const router = useRouter()

// 아이템 클릭 시 상세 조회 페이지로 이동
const clickItemDetail = (item) => {
  if (!item || !item.itemId) {
    return
  }

  console.log('[ItemCard] go detail itemId =', item.itemId)

  // 라우터 설정에 맞게 name / path 쓰기
  router.push({
    name: 'ItemDetail',               // router/index.js 에서 정한 name
    params: { itemId: item.itemId },  // path: /items/:itemId 넘기기
  })
}

// 이미지 URL
const BASE_IMAGE_URL = 'http://localhost:8080'

// /uploads..., uploads... 둘 다 처리 + http로 시작하는 절대 URL도 처리
const toFullImageUrl = (raw) => {
  if (!raw) return ''

  // 이미 절대 URL이면 그대로 사용
  if (raw.startsWith('http://') || raw.startsWith('https://')) {
    return raw
  }

  // 앞에 슬래시 있든 없든 항상 "/uploads/..." 형태로 통일
  const path = raw.startsWith('/') ? raw : `/${raw}`

  return BASE_IMAGE_URL + path
}

// 이미지 URL
const displayImage = computed(() => {
  const url = props.item.imageURL
  console.log('imageURL from API:', url)
  return toFullImageUrl(url)
})


// 상품명
const displayName = computed(() => {
  return props.item.itemName
})

// 태그(카테고리)
const displayTag = computed(() => {
  console.log(props.item.category)
  return props.item.category
})

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

.truncate {
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;

  display: -webkit-box;
  -webkit-line-clamp: 2;        /* 최대 2줄 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;

  line-height: 1.3;             /* 줄간격 */
  max-height: calc(1.3em * 2);  /* 2줄까지만 보이도록 높이 제한 */
}
</style>
