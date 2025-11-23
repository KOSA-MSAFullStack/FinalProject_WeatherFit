<!-- src/views/ProductDetail.vue -->
<template>
  <div>
    <main class="wrap">
      <!-- 1. 상품 상세 -->
      <section class="panel">
        <h1 class="text-2xl font-bold text-gray-900" style="margin-bottom: 15px">상품 상세</h1>

        <!-- 레이아웃 -->
        <div class="detail-layout">
            <!-- 왼쪽: 큰 이미지 -->
            <div 
            class="gallery" 
            :style="{
                backgroundImage: item.imageURL ? `url('${item.imageURL}')` : 'none',
                backgroundSize: 'cover',
                backgroundPosition: 'center'
            }"
            ></div>

            <!-- 오른쪽: 정보 카드 (grid의 두 번째 칸) -->
            <div class="info-card">
            <!-- 분류 뱃지 + 상품명 -->
            <div class="info-header">
                <span class="badge-class">{{ item.classification }}</span>
                <h2 class="item-name">
                {{ item.itemName }}
                </h2>
            </div>

            <!-- 카테고리 / 상품코드 -->
            <div class="meta-list">
                <div class="meta-row">
                <span class="meta-label">분류</span>
                <span class="meta-value">{{ item.classification }}</span>
                </div>
                <div class="meta-row">
                <span class="meta-label">카테고리</span>
                <span class="meta-value">{{ item.category }}</span>
                </div>
                <div class="meta-row">
                <span class="meta-label">성별</span>
                <span class="meta-value">{{ item.gender }}</span>
                </div>
                <div class="meta-row">
                <span class="meta-label">상품코드</span>
                <span class="meta-value">{{ item.itemCode }}</span>
                </div>
            </div>

            <!-- 이런 날씨에 좋아요 -->
            <div class="info-weather">
                <h3>이런 날씨에 좋아요</h3>
                <div class="weather-cards">
                <div class="weather-card">
                    <div class="weather-label">계절</div>
                    <div class="weather-value">{{ item.seasons.join(', ') }}</div>
                </div>
                <div class="weather-card">
                    <div class="weather-label">최저기온 / 최고기온</div>
                    <div class="weather-value">
                    {{ item.minTemperature }}°C ~ {{ item.maxTemperature }}°C
                    </div>
                </div>
                </div>
            </div>

            <!-- 가격 + 버튼 -->
            <div class="info-bottom">
                <div class="price-block">
                <div class="price-main">
                    {{ item.price?.toLocaleString() }}원
                </div>
                </div>

                <div class="info-actions">
                <button @click="handleAddToCart" class="btn">장바구니</button>
                </div>
            </div>
            </div>
        </div>
      </section>

      <!-- 3. AI 설명 -->
      <section class="panel">
        <h1 class="text-2xl font-bold text-gray-900" style="margin-bottom: 15px">상품에 대한 AI 설명</h1>
        <div class="explain">
          <h3 class="text-lg font-semibold text-gray-900">총평</h3>
          <p>{{ aiExplanation.summary }}</p>
          <div class="hr"></div>

          <h3 class="text-lg font-semibold text-gray-900">권장 기온대</h3>
          <ul class="bullet">
            <li v-for="(temp, idx) in aiExplanation.temperatures" :key="idx">
              {{ temp }}
            </li>
          </ul>
          <div class="hr"></div>

          <h3 class="text-lg font-semibold text-gray-900">상황별 보완 팁</h3>
          <ul class="bullet">
            <li v-for="(tip, idx) in aiExplanation.situationTips" :key="idx">
              {{ tip }}
            </li>
          </ul>
          <div class="hr"></div>

          <h3 class="text-lg font-semibold text-gray-900">체질별 가이드</h3>
          <ul class="bullet">
            <li v-for="(guide, idx) in aiExplanation.bodyTypeGuides" :key="idx">
              {{ guide }}
            </li>
          </ul>
          <div class="hr"></div>

          <h3 class="text-lg font-semibold text-gray-900">함께 코디하면 좋은 아이템</h3>
          <ul class="bullet">
            <li v-for="(coord, idx) in aiExplanation.coordinations" :key="idx">
              {{ coord }}
            </li>
          </ul>
        </div>
      </section>

      <!-- Review section -->
      <section class="panel review">
        <h1 class="text-2xl font-bold text-gray-900" style="margin-bottom: 15px">리뷰</h1>

        <!-- 평점 헤더 -->
        <div class="stars">
          <div>★★★★★</div>
          <div class="big">{{ reviewStats.avgScore }}</div>
          <div class="muted">총 <span>{{ reviewStats.count }}</span>건 리뷰</div>
        </div>

        <!-- AI 요약 -->
        <div v-if="item.reviewAiSummary" class="ai-summary">
          <div class="head">AI가 최근 리뷰를 요약했어요</div>
          <p style="margin:6px 0 0 0;">
            {{ item.reviewAiSummary }}
          </p>
        </div>

        <!-- 항목별 만족도 -->
        <div class="bargrid">
          <div class="meter">
            <div class="k">착용한 날의 날씨</div>
            <div 
              v-for="(item, idx) in reviewStats.weatherConditions" 
              :key="idx"
              class="row"
            >
              <span>{{ item.label }}</span>
              <div class="track">
                <div 
                  class="fill" 
                  :style="{ width: item.percent + '%', background: item.color || '' }"
                ></div>
              </div>
              <span>{{ item.percent }}%</span>
            </div>
          </div>

          <div class="meter">
            <div class="k">날씨 체감</div>
            <div 
              v-for="(item, idx) in reviewStats.temperatureFeel" 
              :key="idx"
              class="row"
            >
              <span>{{ item.label }}</span>
              <div class="track">
                <div 
                  class="fill" 
                  :style="{ width: item.percent + '%', background: item.color || '' }"
                ></div>
              </div>
              <span>{{ item.percent }}%</span>
            </div>
          </div>

          <div class="meter">
            <div class="k">실내 착용감</div>
            <div 
              v-for="(item, idx) in reviewStats.indoorComfort" 
              :key="idx"
              class="row"
            >
              <span>{{ item.label }}</span>
              <div class="track">
                <div 
                  class="fill" 
                  :style="{ width: item.percent + '%', background: item.color || '' }"
                ></div>
              </div>
              <span>{{ item.percent }}%</span>
            </div>
          </div>
        </div>

        <!-- 개별 리뷰 리스트 -->
        <div class="rev-list">
          <div v-for="(review, idx) in reviews" :key="idx" class="rev">
            <div class="meta">
              {{ review.stars }} 
              <span style="margin-left:6px">{{ review.score }}</span> 
              · {{ review.author }} 
              · {{ review.date }} 
              · {{ review.weather }}
            </div>
            <div class="body">{{ review.content }}</div>
          </div>
        </div>

        <!-- 페이지네이션 UI -->
        <div v-if="totalPages > 1" class="flex justify-center items-center mt-8 space-x-2">
          <!-- 이전 페이지 버튼 -->
          <button 
            @click="changePage(currentPage - 1)" 
            :disabled="currentPage === 0"
            class="px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 hover:text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            이전
          </button>
          
          <!-- 페이지 번호 버튼 -->
          <button 
            v-for="page in totalPages" 
            :key="page" 
            @click="changePage(page - 1)"
            :class="[
              'px-4 py-2 leading-tight border rounded-lg',
              (page - 1) === currentPage 
                ? 'bg-blue-500 text-white border-blue-500' 
                : 'bg-white text-gray-500 border-gray-300 hover:bg-gray-100 hover:text-gray-700'
            ]"
          >
            {{ page }}
          </button>
          
          <!-- 다음 페이지 버튼 -->
          <button 
            @click="changePage(currentPage + 1)" 
            :disabled="currentPage >= totalPages - 1"
            class="px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 rounded-lg hover:bg-gray-100 hover:text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            다음
          </button>
        </div>

      </section>
    </main>

    <footer class="wrap footer-style">
      © WeatherFit — 상품 상세 페이지
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { getItemDetail, addToCart } from '@/api/itemApi'
import api from "@/utils/axios";

const route = useRoute()
const router = useRouter()

const currentPage = ref(0);   // 현재 페이지 (0부터 시작)
const pageSize = 5;           // 페이지 당 보여줄 항목 수
const totalElements = ref(0); // 전체 리뷰 수
const totalPages = ref(0);    // 전체 페이지 수

// ----------------------
// 1. 상품 상세 조회 (Item Detail)
// ----------------------

// URL에서 itemId 받아오기
const getItemId = computed(() => route.params.itemId)

// API 호출
const { data: itemDetailData, isLoading, isError } = useQuery({
  queryKey: computed(() => ['itemDetail', getItemId.value]),
  queryFn: () => getItemDetail(getItemId.value),
  enabled: computed(() => !!getItemId.value), // itemId 있을 때만 호출
})

// 이미지 URL 가공용
const BASE_IMAGE_URL = 'http://localhost:8080'

// /uploads/xxx.webp → http://localhost:8080/uploads/xxx.webp
const toFullImageUrl = (raw) => {
  if (!raw) return ''
  if (raw.startsWith('http://') || raw.startsWith('https://')) return raw
  return BASE_IMAGE_URL + (raw.startsWith('/') ? raw : `/${raw}`)
}

// 성별 매핑
const genderMap = {
  M: '남성',
  F: '여성',
  C: '남여공용'
}

// 화면에서 바로 쓰기 편하게 가공된 item
const item = computed(() => {
    const data = itemDetailData.value
    if (!data) {
        return {
        itemId: null,
        itemName: '',
        itemCode: '',
        price: 0,
        gender: '',
        imageURL: '',
        aiDescription: '',
        createdAt: '',
        reviewAiSummary: '',
        category: '',
        classification: '',
        seasons: [],
        minTemperature: 0,
        maxTemperature: 0
        }
    }

    return {
    itemId: data.itemId,
    itemName: data.itemName,
    itemCode: data.itemCode,
    price: data.price,
    gender: genderMap[data.gender],
    imageURL: toFullImageUrl(data.imageURL),
    aiDescription: data.aiDescription || '',
    reviewAiSummary: data.reviewAiSummary,
    category: data.category,
    classification: data.classification,
    seasons: data.seasonName,
    minTemperature: data.minTemperature,
    maxTemperature: data.maxTemperature
  }
})

/**
 * 2. aiDescription 파싱
 * 원본 예:
 * 📝 총평 :
 *  ...여러 줄...
 *
 * 🌡️ 권장 기온대 :
 *  - 10–14°C: ...
 *  - 6–10°C: ...
 *
 * ✨ 상황별 보완 팁 :
 *  - ...
 *
 * 👕 체질별 가이드 :
 *  - ...
 *
 * 👗 함께 코디하면 좋은 아이템 :
 *  - ...
 */
function parseAiDescription(text) {
  if (!text) {
    return {
      summary: '',
      temperatures: [],
      situationTips: [],
      bodyTypeGuides: [],
      coordinations: [],
    }
  }

  // 줄 단위로 쪼개서 앞뒤 공백 제거
  const lines = text
    .split(/\r?\n/)
    .map((l) => l.trim())
    .filter((l) => l.length > 0)

  const result = {
    summary: '',
    temperatures: [],
    situationTips: [],
    bodyTypeGuides: [],
    coordinations: [],
  }

  let currentSection = null

  for (const line of lines) {
    // 섹션 헤더 감지
    if (line.startsWith('📝')) {
      currentSection = 'summary'
      continue
    }
    if (line.startsWith('🌡️')) {
      currentSection = 'temperatures'
      continue
    }
    if (line.startsWith('✨')) {
      currentSection = 'situationTips'
      continue
    }
    if (line.startsWith('👕')) {
      currentSection = 'bodyTypeGuides'
      continue
    }
    if (line.startsWith('👗')) {
      currentSection = 'coordinations'
      continue
    }

    // 헤더가 아닌 일반 내용일 때
    switch (currentSection) {
      case 'summary':
        // summary는 여러 줄이면 이어붙이기
        result.summary += (result.summary ? '\n' : '') + line
        break
      case 'temperatures':
        result.temperatures.push(line)
        break
      case 'situationTips':
        result.situationTips.push(line)
        break
      case 'bodyTypeGuides':
        result.bodyTypeGuides.push(line)
        break
      case 'coordinations':
        result.coordinations.push(line)
        break
      default:
      // 섹션이 정해지지 않은 줄은 그냥 버리거나 필요시 summary에 넣어도 됨
        break
    }
  }

  return result
}

// computed로 aiDescription → 구조화된 aiExplanation
const aiExplanation = computed(() => {
  return parseAiDescription(item.value.aiDescription)
})


// 리뷰 선택지 전체 목록(표시 순서까지 고정)
const WEATHER_LABELS = ['맑음', '흐림', '강풍', '비', '눈'];
const TEMP_LABELS    = ['추워요', '시원해요', '보통이에요', '따뜻해요', '더워요'];
const FIT_LABELS     = ['편해요', '보통이에요', '답답해요'];

// 리뷰 통계
const reviewStats = ref({
  avgScore: 0,
  count: 0,
  aiSummary: '',
  weatherConditions: [],
  temperatureFeel: [],
  indoorComfort: []
})

// 데이터 매핑 객체 (프론트 <-> 백엔드)
const weatherMap = { '맑음': 'SUNNY', '흐림': 'CLOUDY', '강풍': 'WINDY', '비': 'RAINY', '눈': 'SNOWY' };
const tempMap = { '추워요': 'COLD', '시원해요': 'COOL', '보통이에요': 'NORMAL', '따뜻해요': 'WARM', '더워요': 'HOT' };
const fitMap = { '편해요': 'COMFORTABLE', '보통이에요': 'NORMAL', '답답해요': 'TIGHT' };

// (역방향 매핑) 백엔드 -> 프론트.
const reverseWeatherMap = Object.fromEntries(Object.entries(weatherMap).map(([k, v]) => [v, k]));
const reverseTempMap = Object.fromEntries(Object.entries(tempMap).map(([k, v]) => [v, k]));
const reverseFitMap = Object.fromEntries(Object.entries(fitMap).map(([k, v]) => [v, k]));

const translateStatisticKeys = (stats, reverseMap) => {
    const translatedStats = {};
    for (const key in stats) {
        if (Object.hasOwnProperty.call(stats, key)) {
            // 영문 키에 해당하는 한국어 값을 찾고, 없으면 원본 키를 사용합니다.
            const koreanKey = reverseMap[key] || key;
            translatedStats[koreanKey] = stats[key];
        }
    }
    return translatedStats;
};

// 개별 리뷰
const reviews = ref([])

// --- 백엔드로부터 리뷰 내역을 가져오는 함수 ---
const fetchReviews = async () => {
  // 함수 내부에서 직접 itemId를 참조하도록 수정
  const itemId = item.value.itemId;

  // itemId가 없으면 함수를 즉시 종료
  if (!itemId) {
    console.log('리뷰 조회 대기: itemId가 아직 없습니다.');
    return;
  }

  try {
    const response = await api.get(`/api/reviews/items/${itemId}`, {
        params: {
            page: currentPage.value,
            size: pageSize,
            sort: 'createdAt,desc'
        }
    });
    const data = response.data;

    totalElements.value = data.reviews.totalElements;
    totalPages.value = data.reviews.totalPages;

    // --- 통계 정보 업데이트 ---
    // 백엔드 응답에서 직접 통계 값을 가져와 reviewStats 상태를 업데이트합니다.
    reviewStats.value.avgScore = data.averageRating.toFixed(1); // 소수점 한 자리
    reviewStats.value.count = data.totalReviews;
    
    // 통계 데이터를 UI에 맞는 형식으로 가공합니다. (가공 함수는 아래에서 만듭니다)
    const translatedWeatherStats = translateStatisticKeys(data.weatherStatistics, reverseWeatherMap);
    const translatedTempStats    = translateStatisticKeys(data.temperatureStatistics, reverseTempMap);
    const translatedFitStats     = translateStatisticKeys(data.indoorFitStatistics, reverseFitMap);

    // 항상 전체 목록(라벨 배열)을 넘긴다
    reviewStats.value.weatherConditions = processStats(translatedWeatherStats, data.totalReviews, WEATHER_LABELS);
    reviewStats.value.temperatureFeel   = processStats(translatedTempStats, data.totalReviews, TEMP_LABELS);
    reviewStats.value.indoorComfort     = processStats(translatedFitStats, data.totalReviews, FIT_LABELS);

    // --- 개별 리뷰 목록 업데이트 ---
    // 리뷰 목록의 경로가 `data.reviews.content`로 변경되었습니다.
    reviews.value = data.reviews.content.map(review => {
      const stars = '★'.repeat(Math.floor(review.ratingScore)) + '☆'.repeat(5 - Math.floor(review.ratingScore));
      const koreanWeather = reverseWeatherMap[review.weather] || review.weather;
      const koreanTemp = reverseTempMap[review.temperature] || review.temperature;
      const koreanFit = reverseFitMap[review.indoorFit] || review.indoorFit;
      
      return {
        stars: stars,
        score: review.ratingScore,
        author: review.userName,
        date: new Date(review.createdAt).toLocaleDateString(),
        weather: `${koreanWeather} / ${koreanTemp} / ${koreanFit}`,
        content: review.contents
      };
    });
      
  } catch (err) {
      console.error('리뷰 정보를 가져오는 데 실패했습니다:', err);
  }
};

/**
 * 백엔드에서 받은 통계 객체를 UI의 진행률 막대 형식에 맞게 변환합니다.
 * @param {object} stats - 예: { "맑음": 10, "흐림": 5 }
 * @param {number} total - 전체 리뷰 수
 * @param {string[]} labels - 보여줄 라벨 전체 목록 (순서 포함)
 * @returns {array} - 예: [{ label: '맑음', percent: 67, color: '#...' }, ...]
 */
const processStats = (stats, total, labels) => {
  // 각 항목에 대한 색상을 미리 정의해두면 좋습니다.
  const colorMap = {
    // 날씨
    '맑음': '#4f9cf9', '흐림': '#9ca3af', '강풍': '#f59e0b', '비': '#3b82f6', '눈': '#a78bfa',
    // 체감
    '추워요': '#60a5fa', '시원해요': '#34d399', '보통이에요': '#a3a3a3', '따뜻해요': '#fbbf24', '더워요': '#f87171',
    // 실내
    '편해요': '#10b981', '답답해요': '#ef4444'
  };

  // stats 에 없는 라벨도 0%로 만들어서 항상 다 보여주기
  return labels.map((label) => {
    const count = stats?.[label] ?? 0;
    const percent = total > 0 ? Math.round((count / total) * 100) : 0;

    return {
      label,
      percent,
      color: colorMap[label] || '#6b7280'
    };
  });
};

// --- 페이지 변경 함수 ---
const changePage = (page) => {
  // 요청하려는 페이지가 유효한 범위 내에 있는지 확인
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page; // 현재 페이지 상태 업데이트
    fetchReviews(); // 해당 페이지 데이터 다시 요청
  }
};

const handleAddToCart = async () => {
  const productId = item.value.itemId;

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

watch(
  () => item.value.itemId, 
  (newItemId) => {
    if (newItemId) {      
      // 페이지 초기화
      currentPage.value = 0;
      
      // 리뷰 목록을 호출
      fetchReviews();
    }
  },
  { immediate: true }
);
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;600;700&display=swap');

:root {
  --bg: #ffffff;
  --panel: #ffffff;
  --line: #e5e7eb;
  --muted: #666666;
  --text: #111111;
  --accent: #4f9cf9;
  --good: #10b981;
  --warn: #f59e0b;
  --chip: #f3f4f6;
  --chip-line: #e5e7eb;
}

* {
  box-sizing: border-box;
}

header {
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, .9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--line);
  z-index: 20;
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
  gap: 10px;
  align-items: center;
}

.logo {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: linear-gradient(135deg, #4f9cf9, #a78bfa);
}

.panel {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, .04);
}

.muted {
  color: var(--muted);
}
/* 전체 좌우 비율: 이미지 2, 정보 3 */
.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(0, 3fr);
  gap: 32px;
  align-items: stretch; 
}

.detail-layout > * {
  height: 100%;
}

@media (max-width: 900px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}

/* 왼쪽 큰 이미지 */
.detail-image {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  height: 100%;
}

.gallery {
  border-radius: 16px;
  aspect-ratio: 3 / 4;
  width: 100%;
  max-width: 520px;     
  background: #f9fafb;
  overflow: hidden;
  height: 100%; 
}

/* 오른쪽 정보 영역 */
.detail-info {
  display: flex;
  align-items: flex-start;
  height: 100%;
}

/* 설명 부분 박스 */
.info-card {
  width: 100%;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  padding: 18px 20px 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  gap: 30px;
  justify-content: space-between;
}

/* 헤더(분류 + 상품명) */
.info-header {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.badge-class {
  align-self: flex-start;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  background: #f3f4ff;
  color: #4f46e5;
}

.item-name {
  font-size: 24px;
  font-weight: 800;
  color: #111827;
  margin: 0;
}

/* 카테고리 / 코드 라인 */
.meta-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
}

.meta-row {
  display: flex;
  justify-content: space-between;
}

.meta-label {
  color: #9ca3af;
}

.meta-value {
  color: #4b5563;
  font-weight: 600;
}

/* 날씨 정보 */
.info-weather h3 {
  font-size: 15px;
  font-weight: 700;
  margin: 0 0 8px;
}

.weather-cards {
  display: flex;
  gap: 8px;
}

.weather-card {
  flex: 1;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.weather-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.weather-value {
  font-size: 14px;
  font-weight: 700;
  color: #111827;
}

/* 가격 + 버튼 아래쪽 블럭 */
.info-bottom {
  margin-top: 4px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 가격 오른쪽 정렬 */
.price-block {
  text-align: right;
  margin-bottom: 30px;
}

.price-main {
  font-size: 26px;
  font-weight: 800;
  color: #111827;
}

.price-sub {
  font-size: 12px;
  color: #9ca3af;
}

/* 버튼 */
.info-actions {
  display: flex;
  justify-content: flex-end;
}

.btn {
  width: 100%;  /* 넓게 꽉 차게 */
  background: linear-gradient(135deg, #4f9cf9, #a78bfa);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  padding: 12px 0;
  cursor: pointer;
}

.btn-wide {
  flex: 0 0 auto;
  min-width: 220px;
  justify-content: center;
  font-size: 15px;
  padding: 10px 18px;
}

.explain {
  background: #fffefc;
  border: 1px dashed var(--line);
  border-radius: 12px;
  padding: 12px;
  line-height: 1.6;
  color: #111827;
}

section {
  margin: 14px 0;
}

.bullet {
  margin: 0;
  padding-left: 18px;
}

.bullet li {
  margin: 5px 0;
}

.hr {
  height: 1px;
  background: var(--line);
  margin: 12px 0;
}

/* AI 설명 박스 전체 폰트 크기 업 */
.explain {
  font-size: 17px;      /* 기본 텍스트(총평, 리스트 등) */
  line-height: 1.7;
}

/* AI 설명 섹션의 소제목들 (총평, 권장 기온대 등) */
.explain h3 {
  font-size: 18px;      /* 기존 16px였다면 +2 정도 */
  margin-bottom: 8px;
}

/* 리스트 항목들 크기 업 */
.explain .bullet li {
  font-size: 17px;
}

.review {
  background: #ffffff;
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, .04);
}

.stars {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #111827;
}

.stars .big {
  font-size: 32px;
}

.ai-summary {
  margin-top: 12px;
  background: linear-gradient(135deg, #eef2ff, #ecfeff);
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 12px;
}

.ai-summary .head {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: #111827;
}

.bargrid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-top: 12px;
}

.meter {
  background: #f3f4f6;
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px;
}

.meter .k {
  font-size: 13px;
  color: #374151;
  margin-bottom: 6px;
}

.meter .row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin: 4px 0;
}

.meter .track {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 999px;
  overflow: hidden;
}

.meter .fill {
  height: 100%;
  background: linear-gradient(90deg, #4f9cf9, #a78bfa);
}

.rev-list {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rev {
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 10px;
}

.rev .meta {
  display: flex;
  gap: 8px;
  align-items: center;
  color: #6b7280;
  font-size: 13px;
}

.rev .body {
  margin-top: 6px;
  line-height: 1.6;
  color: #111827;
}

.footer-style {
  color: #6b7280;
  font-size: 13px;
  border-top: 1px solid var(--line);
  margin-top: 16px;
}
/* 카드 전체 기본 글자 크기 살짝 업 */
.info-card {
  font-size: 15px;
}

/* 상품명 조금 더 강조 */
.item-name {
  font-size: 26px;     /* 기존보다 +2 정도 */
}

/* 카테고리/코드 라인 */
.meta-list {
  font-size: 18px;
}

.meta-row {
  font-size: 17px;
}

/* "이런 날씨에 좋아요" 제목 */
.info-weather h3 {
  font-size: 20px;
}

/* 날씨 카드 안 텍스트 */
.weather-label {
  font-size: 15px;
}

.weather-value {
  font-size: 16px;
}

/* 가격 쪽 */
.price-main {
  font-size: 28px;
}
</style>