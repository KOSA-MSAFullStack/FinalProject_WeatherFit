<!-- author: 김경아 -->
<template>
  <section class="hero3">
    <!-- 오늘 날씨 추천 아우터 -->
    <article
      class="hero"
      :style="heroStyle(outer?.imageURL, true)"
      @click="clickItemDetail(outer)"
    >
      <div class="overlay"></div>
      <div class="content">
        <div class="kicker">오늘 날씨 추천 아우터</div>
        <div class="title">{{ outer?.itemName }}</div>
      </div>
    </article>

    <!-- 오늘 날씨 추천 상의 -->
    <article
      class="hero"
      :style="heroStyle(top?.imageURL, false)"
      @click="clickItemDetail(top)"
    >
      <div class="overlay"></div>
      <div class="content">
        <div class="kicker">오늘 날씨 추천 상의</div>
        <div class="title">{{ top?.itemName }}</div>
      </div>
    </article>

    <!-- 오늘 날씨 추천 하의 -->
    <article
      class="hero"
      :style="heroStyle(bottom?.imageURL, false)"
      @click="clickItemDetail(bottom)"
    >
      <div class="overlay"></div>
      <div class="content">
        <div class="kicker">오늘 날씨 추천 하의</div>
        <div class="title">{{ bottom?.itemName }}</div>
      </div>
    </article>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  // {
  //   itemId: 4,
  //   itemName: "...",
  //   imageURL: "uploads/~~~.webp"
  // }
  outer: {
    type: Object,
    required: true,
  },
  top: {
    type: Object,
    required: true,
  },
  bottom: {
    type: Object,
    required: true,
  },
})

const BASE_IMAGE_URL = 'http://localhost:8080/'

// "uploads/파일.webp" -> "http://localhost:8080/uploads/파일.webp"
const toFullUrl = (url) => {
  if (!url) return ''

  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  const path = url.startsWith('/') ? url.slice(1) : url
  return `${BASE_IMAGE_URL}${path}`
}

const router = useRouter()

// 아이템 클릭 시 상세 조회 페이지로 이동
const clickItemDetail = (item) => {
  if (!item || !item.itemId) {
    return
  }

  console.log('[MainProductCard] go detail itemId =', item.itemId)

  // 라우터 설정에 맞게 name / path 쓰기
  router.push({
    name: 'ItemDetail',               // router/index.js 에서 정한 name
    params: { itemId: item.itemId },  // path: /items/:itemId 넘기기
  })
}

// inline style 부분
const heroStyle = (url, useGradient = false) => {
  if (!url) return {}

  const fullUrl = toFullUrl(url)

  if (useGradient) {
    return {
      backgroundImage:
        `linear-gradient(180deg, rgba(255,255,255,.0), rgba(0,0,0,.08)), url('${fullUrl}')`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
    }
  }

  return {
    backgroundImage: `url('${fullUrl}')`,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
  }
}
</script>

<style scoped>
.hero3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
  margin-top: 14px;
}

@media (max-width: 1000px) {
  .hero3 {
    grid-template-columns: 1fr;
  }
}

.hero {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  aspect-ratio: 3 / 4;
  height: auto;        
  display: flex;
  align-items: flex-end;
  background: linear-gradient(180deg, #f3f4f6, #e5e7eb);
}

/* 오버레이를 아래로 갈수록 더 어둡게 */
.hero .overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0.0),   /* 위쪽은 거의 투명 */
    rgba(0, 0, 0, 0.55)   /* 아래쪽은 꽤 어두움 */
  );
}

.hero .content {
  position: relative;
  padding: 16px;
  width: 100%;
  z-index: 1; /* 오버레이 위에 올라오도록 */
}

/* 글씨를 흰색 + 그림자 */
.kicker {
  font-size: 25px;
  color: #ffffff;
  letter-spacing: 0.4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.55);
}

.title {
  font-size: 30px;
  font-weight: 800;
  margin: 6px 0;
  color: #ffffff;
  text-shadow: 0 3px 6px rgba(0, 0, 0, 0.6);
}
</style>
