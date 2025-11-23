<!-- author: 김경아 -->
<template>
  <div>
    <main class="wrap">
      <!-- 상단 분류 바 -->
      <div class="topbar">
        <div class="classification">
          <!-- Top nav -->
          <div class="topnav">
            <a 
              v-for="classification in classifications" 
              :key="classification.key"
              href="#" 
              :class="['nav-item', { active: activeClassification === classification.key }]"
              @click.prevent="changeClassification(classification.key)"
            >
              {{ classification.label }}
            </a>
          </div>
          <div class="search">
            <input v-model="searchQuery" placeholder="상품 검색" />
            <button class="btn" @click="search">검색</button>
          </div>
        </div>

        <!-- category bar -->
        <div class="subbar">
          <a 
            v-for="(category, idx) in currentCategories" 
            :key="idx"
            href="#"
            :class="{ active: activeCategory === category }"
            @click.prevent="filterByCategory(category)"
          >
            {{ category }}
          </a>
        </div>

        <div class="filters">
          <div class="wrap">
            <!-- 성별 필터 -->
            <div
              class="pill"
              :class="{ blue: activeGender === 'M' }"
              @click="changeGender('M')"
            >
              남성
            </div>
            <div
              class="pill"
              :class="{ blue: activeGender === 'F' }"
              @click="changeGender('F')"
            >
              여성
            </div>
            <!-- 계절 필터 -->
            <div
              class="pill"
              :class="{ blue: activeSeason === '봄' }"
              @click="changeSeason('봄')"
            >
              봄
            </div>
            <div
              class="pill"
              :class="{ blue: activeSeason === '여름' }"
              @click="changeSeason('여름')"
            >
              여름
            </div>
            <div
              class="pill"
              :class="{ blue: activeSeason === '가을' }"
              @click="changeSeason('가을')"
            >
              가을
            </div>
            <div
              class="pill"
              :class="{ blue: activeSeason === '겨울' }"
              @click="changeSeason('겨울')"
            >
              겨울
            </div>
          </div>
        </div>
      </div>

      <!-- 정렬 툴바 -->
      <div class="toolbar">
        <div class="left-info">총 <span>{{ filteredItems.length }}</span>개 상품</div>
        <div class="sort">
          <select v-model="sortType" @change="render">
            <option value="pop">구분</option>
            <option value="low">낮은가격순</option>
            <option value="high">높은가격순</option>
            <!-- <option value="rate">리뷰많은순</option> -->
          </select>
        </div>
      </div>

      <!-- 상품 갤러리 -->
      <div v-if="isLoading">
        로딩 중입니다...
      </div>

      <div v-else-if="isError">
        상품을 불러오는 중 오류가 발생했습니다.
      </div>

      <div v-else class="gallery">
        <div 
          v-for="item in paginatedItems" 
          :key="item.itemIid"
          class="card"
        >
          <div 
            class="thumb"
            :style="{
              backgroundImage: `url('${displayImage(item)}')`,
              backgroundSize: 'cover',
              backgroundPosition: 'center'
            }"
            @click="clickItemDetail(item)"
          >
            <span class="category-badge">{{ item.category }}</span>
          </div>
          <div class="meta">
            <div class="row">
              <div class=item-name @click="clickItemDetail(item)">{{ item.itemName }}</div>
            </div>
            <div class="row">
              <div>{{ item.price.toLocaleString() }}원</div>
              <button @click="handleAddToCart(item)" class="btn cart" style="padding:6px 10px;">장바구니</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이저 -->
      <div class="pager">
        <button class="btn ghost" @click="prevPage" :disabled="page === 0">이전</button>
        <button class="btn" @click="nextPage">다음</button>
      </div>

      <!-- <div class="brandbar">
        <div class="badge">브랜드 정보</div>
        <div class="muted">교환/반품 · 배송정책 · AS 문의: help@brand.com · 02-0000-0000</div>
      </div> -->
    </main>

    <footer class="wrap">데이터는 무신사 스탠다드에서 참고하였습니다.</footer>
  </div>
</template>

<script setup>
import { getAllItem, addToCart, getCategoryData } from '@/api/itemApi'
import { useQuery } from '@tanstack/vue-query'
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 1. API 호출
const { data, isLoading, isError } = useQuery({
  queryKey: ['allItems'],
  queryFn: getAllItem  // 말고 함수 자체를 넘기기
})

// 2. API에서 가져온 data -> items(항상 배열이 되게)
const items = computed(() => {
  // data.value가 아직 로딩 중이면 undefined 일 수 있으니 방어코드
  const raw = data?.value ?? []

  // quantity가 0보다 큰 것만 남기기(품절 처리)
  return raw.filter(p => (p.quantity ?? 0) > 0)
})

console.log(items.value);

// 이미지 URL 가공용
const BASE_IMAGE_URL = 'http://localhost:8080'

const displayImage = item => {
  if (!item.imageURL) return ''

  const cleaned = item.imageURL.trim()        // 🔹 앞뒤 공백 제거
  const path = cleaned.startsWith('/') ? cleaned : `/${cleaned}`

  console.log(cleaned);
  console.log(path);
  return BASE_IMAGE_URL + path
}

// 아이템 클릭 시 상세 조회 페이지로 이동
const clickItemDetail = (item) => {
  if (!item || !item.itemId) {
    return
  }

  console.log('[MainItemCard] go detail itemId =', item.itemId)

  // 라우터 설정에 맞게 name / path 쓰기
  router.push({
    name: 'ItemDetail',               // router/index.js 에서 정한 name
    params: { itemId: item.itemId },  // path: /items/:itemId 넘기기
  })
}

// 분류/카테고리 정보
// FE key → DB 저장 값
const classificationMap = {
  outer: '아우터',
  top: '상의',
  bottom: '하의'
}

const classifications = ref([
  { key: 'all', label: '전체' },
  { key: 'outer', label: '아우터' },
  { key: 'top', label: '상의' },
  { key: 'bottom', label: '하의' }
])

// 카테고리 API 호출
const { data: categoryRes, isLoading: isCatLoading, isError: isCatError } = useQuery({
  queryKey: ['categoryData'],
  queryFn: getCategoryData
})

// 백엔드 응답 -> 프론트에서 쓰기 좋은 형태로 변환
const categoriesByClassification = computed(() => {
  const cd = categoryRes?.value?.categoryData ?? {}

  const outerList  = (cd['아우터'] ?? []).map(c => c.category)
  const topList    = (cd['상의'] ?? []).map(c => c.category)
  const bottomList = (cd['하의'] ?? []).map(c => c.category)

  return {
    all: ['전체'],
    outer: ['전체', ...outerList],
    top: ['전체', ...topList],
    bottom: ['전체', ...bottomList]
  }
})


// 분류 선택 상태 초기값
const activeClassification = ref('all')

// 카테고리 선택 상태 초기값
const activeCategory = ref('전체')

// 성별 필터링
const activeGender = ref('전체')   // '전체' | '남성' | '여성'

// 계절 필터링
const activeSeason = ref('전체')   // '전체' | '봄' | '여름' | '가을' | '겨울'

// 검색 쿼리
const searchQuery = ref('')

// 정렬 타입
const sortType = ref('pop')

// 선택한 분류에 따른 현재 카테고리들
const currentCategories = computed(() => {
  return categoriesByClassification.value[activeClassification.value] || ['전체']
})

// 성별 바꾸는 메서드
const changeGender = (g) => {
  // 같은 걸 한 번 더 누르면 전체로 초기화 하고 싶으면 이렇게:
  if (activeGender.value === g) {
    activeGender.value = '전체'
  } else {
    activeGender.value = g
  }
  page.value = 0
}

// 계절 바꾸는 메서드
const changeSeason = (s) => {
  // 같은 계절을 한 번 더 누르면 '전체'로 초기화
  if (activeSeason.value === s) {
    activeSeason.value = '전체'
  } else {
    activeSeason.value = s
  }
  page.value = 0
}

// 3. API 데이터 기반 필터/정렬
const filteredItems = computed(() => {
  // 1) API에서 온 전체 아이템 배열 복사
  let result = items.value.slice()

  // 2) 분류(아우터/상의/하의) 필터
  if (activeClassification.value !== 'all') {
    const target = classificationMap[activeClassification.value]  // 'outer' → '아우터' 등

    // 예시: 백엔드에서 classification "아우터" | "상의" | "하의"
    result = result.filter(p => (p.classification || '').trim() === target)
  }

  // 3) 카테고리(바람막이/패딩/코트 ...) 필터
  if (activeCategory.value !== '전체') {
    result = result.filter(p =>
      (p.category || '').trim() === activeCategory.value
    )
  }

  // 4) 성별 필터
  if (activeGender.value !== '전체') {
    result = result.filter(p =>
      (p.gender || '').trim() === activeGender.value
    )
  }

// 5) 계절 필터
  if (activeSeason.value !== '전체') {
    result = result.filter(p =>
      Array.isArray(p.seasonName) && p.seasonName.includes(activeSeason.value)
    )
  }

  // 6) 검색 필터 (상품명 기준)
  if (searchQuery.value) {
    result = result.filter(p =>
      p.itemName?.includes(searchQuery.value)
    )
  }

  // 7) 정렬
  if (sortType.value === 'low') {
    result.sort((a, b) => a.price - b.price)
  } else if (sortType.value === 'high') {
    result.sort((a, b) => b.price - a.price)
  }

  return result
})

// 4. 페이지네이션된 상품
const page = ref(0)
const pageSize = ref(16)

const paginatedItems = computed(() => {
  const start = page.value * pageSize.value // page가 0이면 0 * 12 = 0 -> 0 ~ 11번째 아이템
  return filteredItems.value.slice(start, start + pageSize.value)
})

// 메서드
const changeClassification = (key) => {
  activeClassification.value = key
  activeCategory.value = '전체'   // 분류 바뀌면 카테고리 필터 리셋
  page.value = 0
}

// 카테고리 필터링
const filterByCategory = (category) => {
  console.log('카테고리 선택:', category)
  activeCategory.value = category // 선택 상태 업데이트
  page.value = 0                  // 새 필터 기준 첫 페이지로
}

const search = () => {
  page.value = 0
  console.log('검색:', searchQuery.value)
}

const render = () => {
  // 정렬 변경 시 자동으로 computed가 재계산됨
  console.log('렌더링')
}

// 이전 페이지
const prevPage = () => {
  if (page.value > 0) {
    page.value--
  }
}

// 다음페이지
const nextPage = () => {
  const maxPage = Math.ceil(filteredItems.value.length / pageSize.value) - 1
  if (page.value < maxPage) {
    page.value++
  }
}

const handleAddToCart = async (item) => {
  const productId = item.itemId;

  if (!productId) {
    alert('상품 정보가 올바르지 않습니다.');
    return;
  }

  try {
    const response = await addToCart(productId);

    if (response.status === 201) {
      if (confirm('장바구니에 상품이 추가되었습니다. 장바구니로 이동하시겠습니까?')) {
        router.push('/cart');
      }
    }
  } catch (error) {
    console.error('장바구니 추가 실패:', error);
    const errorMessage = error.response?.data?.message || '장바구니 추가에 실패했습니다.';
    if (error.response?.status === 401) {
        alert('로그인이 필요합니다.');
        router.push('/login');
    } else {
        alert(errorMessage);
    }
  }
};

</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;600;700&display=swap');

:root {
  --bg: #ffffff;
  --text: #111111;
  --muted: #666666;
  --line: #e5e7eb;
  --card: #ffffff;
  --accent1: #4f9cf9;
  --accent2: #a78bfa;
  --pill: #f3f4f6;
  --badge: #16a34a;
}

* {
  box-sizing: border-box;
}

header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, .9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--line);
}

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}

.nav {
  margin: 0 auto;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--accent1), var(--accent2));
}

.kpis {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.kpi {
  background: #ffffff;
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px;
}

.kpi .v {
  font-weight: 700;
  color: #111827;
}

input, select, button {
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
}

input::placeholder {
  color: #9ca3af;
}

.btn {
  background: linear-gradient(135deg, var(--accent1), var(--accent2));
  color: #ffffff;
  border: none;
  font-weight: 700;
  cursor: pointer;
}

.cart {
  background: #000000;
}

.search {
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn.ghost {
  background: #ffffff;
  border: 1px solid var(--line);
  color: #111827;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.muted {
  color: var(--muted);
}

.topbar {
  top: 0;
  z-index: 30;
  background: #fff;
  border-bottom: 1px solid var(--line);
}

.classification {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.topnav {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  font-size: 20px;
}

.topnav .nav-item {
  color: #111827;
  text-decoration: none;
  padding: 6px 2px;
}

.topnav .nav-item.active {
  font-weight: 700;
  color: #2563eb;
  border-bottom: 2px solid #2563eb;
}

.subbar {
  margin-top: 8px;
  padding: 8px 0;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  cursor: pointer;
}

.subbar a.active {
  font-weight: 700;
  color: #2563eb;
  border-bottom: 2px solid #2563eb;
}

.filters {
  border-top: 1px solid var(--line);
  border-bottom: 1px solid var(--line);
  background: #fff;
}

.filters .wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 8px 16px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
}

.pill {
  background: var(--pill);
  border: 1px solid var(--line);
  padding: 6px 10px;
  font-size: 15px;
  white-space: nowrap;
  cursor: pointer;
}

.pill.blue {
  border-color: #c7d2fe;
  background: #eff6ff;
  color: #1e40af;
}

.toolbar {
  max-width: 1200px;
  margin: 12px auto 12px;
  padding: 0 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-info {
  color: var(--muted);
  font-size: 16px;
}

.sort {
  display: flex;
  gap: 8px;
  align-items: center;
}

.gallery {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

@media(max-width:1100px) {
  .gallery {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media(max-width:800px) {
  .gallery {
    grid-template-columns: repeat(2, 1fr);
  }
}

.card {
  background: #ffffff;
  border: 1px solid var(--line);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, .04);
}

.thumb {
  aspect-ratio: 3/4;
  background: linear-gradient(180deg, #f3f4f6, #e5e7eb);
  cursor: pointer;
  position: relative;
}

.category-badge {
  position: absolute;
  left: 8px;
  bottom: 8px;
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 4px;
  background: #06b6d4;  /* 무신사 느낌의 파란색 */
  color: #ffffff;
}

.meta {
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brandbar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 20px;
}

.badge {
  font-size: 11px;
  color: #065f46;
  background: #d1fae5;
  padding: 3px 8px;
  border-radius: 999px;
}

.pager {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 20px;
}

.pager button {
  padding: 8px 12px;
}

.item-name {
  cursor: pointer;
  font-size: 16px;
  font-weight: 600;
  max-width: 190px;
}

footer {
  color: #6b7280;
  font-size: 13px;
  padding: 28px 0;
  border-top: 1px solid var(--line);
  margin-top: 16px;
}
</style>