<template>
  <!-- 전체 페이지 배경 및 폰트 설정 -->
  <div class="bg-gray-50 min-h-screen font-sans text-gray-800">

    <!-- 메인 컨텐츠 영역 -->
    <main class="max-w-7xl mx-auto p-4">
      <div class="grid grid-cols-1 lg:grid-cols-[240px_1fr] gap-6 mt-4">
        
        <!-- 사이드바 메뉴 -->
        <aside class="bg-white border border-gray-200 rounded-xl p-4 h-fit sticky top-20">
          <h3 class="text-lg font-semibold mb-3 px-2">마이페이지</h3>
          <ul>
            <li>
              <a @click.prevent="activeTab = 'orders'" href="#" 
                 :class="[
                   'block px-3 py-2.5 rounded-lg cursor-pointer transition-colors mb-1 text-sm font-medium',
                   activeTab === 'orders' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100'
                 ]">
                주문 내역
              </a>
            </li>
            <li>
              <a @click.prevent="activeTab = 'profile'" href="#"
                 :class="[
                   'block px-3 py-2.5 rounded-lg cursor-pointer transition-colors mb-1 text-sm font-medium',
                   activeTab === 'profile' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100'
                 ]">
                기본 정보
              </a>
            </li>
            <li>
              <a @click.prevent="activeTab = 'reviews'" href="#"
                 :class="[
                   'block px-3 py-2.5 rounded-lg cursor-pointer transition-colors mb-1 text-sm font-medium',
                   activeTab === 'reviews' ? 'bg-blue-500 text-white' : 'hover:bg-gray-100'
                 ]">
                리뷰 관리
              </a>
            </li>
          </ul>
        </aside>

        <!-- 메인 컨텐츠 -->
        <div>
          <!-- 주문 내역 페이지 -->
          <div v-if="activeTab === 'orders'">
            <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm mb-6">
              <h2 class="text-2xl font-bold mb-4">안녕하세요, {{ user.name }}님! 👋</h2>
              <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
                  <div class="text-2xl font-bold text-gray-900">{{ totalElements }}</div>
                  <div class="text-xs text-gray-500 mt-1">총 주문</div>
                </div>
                <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
                  <div class="text-2xl font-bold text-gray-900">8</div>
                  <div class="text-xs text-gray-500 mt-1">작성 리뷰</div>
                </div>
                <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
                  <div class="text-2xl font-bold text-gray-900">24</div>
                  <div class="text-xs text-gray-500 mt-1">찜 목록</div>
                </div>
                <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
                  <div class="text-2xl font-bold text-gray-900">3</div>
                  <div class="text-xs text-gray-500 mt-1">장바구니</div>
                </div>
              </div>
            </div>

            <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm mb-6">
              <h2 class="text-xl font-bold mb-4">주문 내역 (총 {{ totalElements }}건)</h2>
              
              <!-- 로딩/에러 상태 표시 -->
              <div v-if="isLoading" class="text-center py-10 text-gray-500">
                <svg class="animate-spin h-5 w-5 text-blue-500 inline-block mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                주문 내역을 불러오는 중...
              </div>
              <div v-else-if="error" class="text-center py-10 text-red-500 border border-red-300 bg-red-50 rounded-lg p-4">
                주문 내역을 불러오는 데 실패했습니다. 다시 시도해주세요.
              </div>
              <div v-else-if="totalElements === 0" class="text-center py-10 text-gray-500 border border-gray-300 bg-gray-50 rounded-lg p-4">
                최근 6개월 동안 주문 내역이 없습니다.
              </div>

              <!-- 주문 목록 렌더링 (Order 기준으로 그룹화) -->
              <div v-else class="space-y-6">
                <div v-for="order in orders" :key="order.orderId" class="border border-gray-300 rounded-xl overflow-hidden">
                  
                  <!-- 주문 헤더 (날짜 및 주문 번호) -->
                  <div class="bg-gray-100 p-3 flex justify-between items-center text-sm font-semibold text-gray-700 border-b border-gray-300">
                    <div class="flex items-center gap-4">
                      <span>{{ formatDate(order.orderDate) }} 주문</span>
                      <span class="text-xs text-gray-500 font-normal">| 주문번호: {{ order.orderNo }}</span>
                    </div>
                  </div>

                  <!-- 주문 상품 항목 목록 (OrderItem) -->
                  <div class="divide-y divide-gray-200">
                    <div v-for="item in order.orderItems" :key="item.orderItemId" class="p-4 flex gap-4 transition-colors hover:bg-gray-50">
                      
                      <!-- 상품 이미지 -->
                      <div class="w-20 h-20 rounded-md bg-gray-100 shrink-0 border border-gray-200 overflow-hidden">
                        <!-- 실제 이미지 경로를 사용합니다. -->
                        <img 
                          :src="getFullImageUrl(item.itemImage)" 
                          :alt="item.itemName" 
                          class="w-full h-full object-cover"
                        >
                      </div>
                      
                      <!-- 상품 정보 -->
                      <div class="grow">
                        <p class="font-bold text-gray-800 line-clamp-1">{{ item.itemName }}</p>
                        <p class="text-xs text-gray-500 mt-1">수량: {{ item.quantity }}개 | 금액: {{ (item.itemPrice * item.quantity).toLocaleString() }}원</p>
                        <p class="text-sm font-semibold text-gray-700 mt-1">주문 완료</p>
                        
                        <!-- 버튼 영역 -->
                        <div class="mt-2 space-x-2">
                          <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-white text-gray-700 border border-gray-300 hover:bg-gray-100">상세 보기</button>
                          <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-blue-500 text-white hover:bg-blue-600">리뷰 쓰기</button>
                        </div>
                      </div>
                    </div>
                  </div>
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
            </div>
          </div>

          <!-- 기본 정보 페이지 -->
          <div v-if="activeTab === 'profile'">
            <!-- 기존 패널 스타일 유지 -->
            <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm mb-6">
              <h2 class="text-xl font-bold mb-5">기본 정보</h2>
              
              <form @submit.prevent="saveProfile" class="space-y-5">
                
                <!-- 이름 -->
                <div class="space-y-1">
                  <label for="name" class="block font-semibold text-sm text-gray-700">이름</label>
                  <input 
                    id="name" 
                    type="text" 
                    v-model="user.name" 
                    class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                  />
                </div>

                <!-- 이메일 (아이디) -->
                <div class="space-y-1">
                  <label for="email" class="block font-semibold text-sm text-gray-700">이메일 (아이디)</label>
                  <input 
                    id="email" 
                    type="email" 
                    v-model="user.email" 
                    class="w-full bg-gray-100 border border-gray-300 rounded-xl p-3 text-base outline-none cursor-not-allowed"
                    readonly 
                    disabled
                  />
                </div>
                
                <!-- 생년월일 -->
                <div class="space-y-1">
                  <label for="birth" class="block font-semibold text-sm text-gray-700">생년월일</label>
                  <input 
                    id="birth" 
                    type="date" 
                    v-model="user.birth" 
                    class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                  />
                </div>

                <!-- 성별 -->
                <div class="space-y-1">
                  <label class="block font-semibold text-sm text-gray-700">성별</label>
                  <div class="flex gap-2">
                    <input type="radio" id="MALE" value="MALE" v-model="user.gender" class="hidden" />
                    <label 
                      for="MALE" 
                      :class="[
                        'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                        { 'bg-blue-100 border-blue-500 text-blue-700 font-semibold': user.gender === 'MALE' }
                      ]"
                    >
                      남성
                    </label>
                    
                    <input type="radio" id="FEMALE" value="FEMALE" v-model="user.gender" class="hidden" />
                    <label 
                      for="FEMALE" 
                      :class="[
                        'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                        { 'bg-blue-100 border-blue-500 text-blue-700 font-semibold': user.gender === 'FEMALE' }
                      ]"
                    >
                      여성
                    </label>
                  </div>
                </div>

                <!-- 연락처 -->
                <div class="space-y-1">
                  <label for="phone" class="block font-semibold text-sm text-gray-700">연락처</label>
                  <input 
                    id="phone" 
                    type="tel" 
                    v-model="user.phone" 
                    @input="formatPhoneNumber"
                    placeholder="010-0000-0000" 
                    maxlength="13"
                    class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
                  />
                </div>

                <!-- 주소 -->
                <div class="space-y-1">
                  <label class="block font-semibold text-sm text-gray-700">주소</label>
                  <div class="grid grid-cols-3 gap-2">
                    <input 
                      type="text" 
                      v-model="user.zipCode"
                      placeholder="우편번호" 
                      readonly 
                      class="col-span-2 bg-gray-100 border border-gray-300 rounded-xl p-3 text-base outline-none" 
                    />
                    <button 
                      type="button" 
                      @click="findAddress" 
                      class="col-span-1 bg-white border border-gray-300 text-gray-700 rounded-xl font-medium text-sm hover:bg-gray-100 transition duration-150"
                    >
                      주소 찾기
                    </button>
                  </div>
                  <input 
                    type="text" 
                    v-model="user.baseAddress"
                    placeholder="기본 주소" 
                    readonly 
                    class="w-full bg-gray-100 border border-gray-300 rounded-xl p-3 text-base mt-2 outline-none" 
                  />
                  <input 
                    type="text" 
                    v-model="user.detailAddress"
                    placeholder="상세 주소" 
                    class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base mt-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition" 
                  />
                </div>

                <!-- 날씨 민감도 -->
                <div class="space-y-1">
                  <label class="block font-semibold text-sm text-gray-700">날씨 민감도</label>
                  <div class="flex gap-2 flex-wrap">
                    <input type="radio" id="COLD" value="COLD" v-model="user.temperatureSensitivity" class="hidden">
                    <label 
                      for="COLD" 
                      :class="[
                        'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                        { 'bg-blue-100 border-blue-500 text-blue-700 font-semibold': user.temperatureSensitivity === 'COLD' }
                      ]"
                    >
                      추위 민감
                    </label>
                    
                    <input type="radio" id="NORMAL" value="NORMAL" v-model="user.temperatureSensitivity" class="hidden">
                    <label 
                      for="NORMAL" 
                      :class="[
                        'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                        { 'bg-blue-100 border-blue-500 text-blue-700 font-semibold': user.temperatureSensitivity === 'NORMAL' }
                      ]"
                    >
                      보통
                    </label>
                    
                    <input type="radio" id="HOT" value="HOT" v-model="user.temperatureSensitivity" class="hidden">
                    <label 
                      for="HOT" 
                      :class="[
                        'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                        { 'bg-blue-100 border-blue-500 text-blue-700 font-semibold': user.temperatureSensitivity === 'HOT' }
                      ]"
                    >
                      더위 민감
                    </label>
                  </div>
                </div>
                
                <!-- 버튼 영역 (기존 스타일 유지) -->
                <div class="flex justify-end space-x-2 pt-4">
                  <button type="button" @click="handleCancel" class="px-4 py-2 rounded-md font-semibold text-sm transition-colors duration-200 bg-white text-gray-700 border border-gray-300 hover:bg-gray-50">취소</button>
                  <button type="submit" class="px-4 py-2 rounded-md font-semibold text-sm transition-colors duration-200 bg-blue-500 text-white hover:bg-blue-600">저장</button>
                </div>
              </form>
            </div>
          </div>

          <!-- 리뷰 관리 페이지 -->
          <div v-if="activeTab === 'reviews'">
            <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm mb-6">
              <h2 class="text-xl font-bold mb-4">리뷰 관리</h2>
              <h3 class="text-base font-semibold mb-3">작성한 리뷰 ({{ reviews.length }})</h3>
              <div class="space-y-4">
                <div v-for="review in reviews" :key="review.id" class="border border-gray-200 bg-white rounded-lg p-4">
                  <div class="flex justify-between items-center mb-2">
                    <p class="font-semibold text-sm">{{ review.productName }}</p>
                    <p class="text-xs text-gray-500">{{ review.date }}</p>
                  </div>
                  <div class="text-yellow-400 mb-2">★★★★★</div>
                  <p class="text-sm text-gray-600 leading-relaxed mb-3">{{ review.text }}</p>
                  <div class="flex space-x-2">
                    <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-white text-gray-700 border border-gray-300 hover:bg-gray-50">수정</button>
                    <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-red-500 text-white hover:bg-red-600">삭제</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
    </main>
    
    <!-- 푸터 -->
    <!-- <footer class="max-w-7xl mx-auto py-5 px-4 mt-10 border-t border-gray-200 text-center text-xs text-gray-500">
      © WeatherFit Shop · 마이페이지
    </footer> -->
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/utils/axios'; // 인터셉터가 설정된 axios 인스턴스를 가져옵니다.
import { useAuthStore } from '@/store/authStore'; // 로그아웃 처리를 위해 스토어를 사용합니다.

const router = useRouter();
const authStore = useAuthStore();
const activeTab = ref('orders');

// 주문 내역 관련 상태
const orders = ref([]); // API 응답의 content (주문 목록)
const isLoading = ref(false);
const error = ref(null);
const currentPage = ref(0); // 현재 페이지 (0부터 시작)
const pageSize = 5;       // 페이지 당 보여줄 항목 수
const totalElements = ref(0); // 전체 주문 수
const totalPages = ref(0);    // 전체 페이지 수

// 이미지 URL 완성 로직
// api 인스턴스에서 baseURL (예: http://localhost:8080)을 가져옵니다.
const API_BASE_URL = api.defaults.baseURL || '';

// 헬퍼 함수: 상대 경로를 완전한 이미지 URL로 변환
const getFullImageUrl = (relativePath) => {
    // 상대 경로가 없으면 placeholder 반환
    if (!relativePath) {
        return 'https://placehold.co/80x80/f1f5f9/94a3b8?text=Img'; 
    }
    // 기본 URL과 상대 경로를 조합 (예: http://localhost:8080/uploads/M53002APLO0.webp)
    return `${API_BASE_URL}${relativePath}`;
};

// originalUser: DB에서 가져온 원본. '저장' 시에만 업데이트됩니다.
const originalUser = ref({
  name: '', email: '', birth: '', gender: '', phone: '',
  zipCode: '', baseAddress: '', detailAddress: '', temperatureSensitivity: '',
});

// user: v-model과 연결되어 사용자가 수정하는 데이터.
const user = ref({
  name: '', email: '', birth: '', gender: '', phone: '',
  zipCode: '', baseAddress: '', detailAddress: '', temperatureSensitivity: '',
});

// 임시 리뷰 데이터
const reviews = ref([
  {
    id: 1,
    productName: '울 블렌드 인타르시아 니트 탑',
    date: '2025.10.29',
    rating: 5,
    text: '날씨가 쌀쌀해지는 요즘 입기 딱 좋아요! 두께감도 적당하고 디자인도 심플해서 어떤 옷이랑도 잘 어울려요. 특히 날씨 추천 기능 덕분에 구매했는데 정말 만족스러워요 👍'
  },
  {
    id: 2,
    productName: '라이트 트렌치 코트',
    date: '2025.10.21',
    rating: 4,
    text: '방수 기능이 생각보다 좋네요. 비 오는 날 입어봤는데 물이 스며들지 않았어요. 다만 사이즈가 약간 크게 나온 것 같아요. 한 치수 작게 주문하시는 걸 추천해요!'
  }
]);

const resetProfileForm = () => {
  // 원본 객체를 복사하여 수정용 객체에 할당합니다.
  user.value = { ...originalUser.value };
  // 전화번호 포매팅도 다시 적용합니다.
  formatPhoneNumber();
};

// 전화번호 형식 자동 변환 함수 (@input 이벤트에 연결)
const formatPhoneNumber = () => {
  let raw = user.value.phone.replace(/[^0-9]/g, '');
  let formatted = '';

  // 11자리 초과 입력 방지
  if (raw.length > 11) {
    raw = raw.substring(0, 11);
  }

  // 길이에 따라 하이픈 추가
  if (raw.length > 3 && raw.length <= 7) {
    formatted = `${raw.slice(0, 3)}-${raw.slice(3)}`;
  } else if (raw.length > 7) {
    formatted = `${raw.slice(0, 3)}-${raw.slice(3, 7)}-${raw.slice(7)}`;
  } else {
    formatted = raw;
  }

  // 포매팅된 값을 다시 user.phone에 할당
  user.value.phone = formatted;
};

const formatDate = (datetime) => {
    if (!datetime) return '';
    const date = new Date(datetime);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).replace(/\. /g, '.').replace(/\.$/, ''); // 2025. 11. 16. -> 2025.11.16
};

// --- 백엔드로부터 주문 내역을 가져오는 함수 ---
const fetchOrderHistory = async () => {
    isLoading.value = true;
    error.value = null;

    try {
        const response = await api.get('/api/orders', {
            params: {
                page: currentPage.value,
                size: pageSize,
                sort: 'orderDate,desc' // 수정: Order 엔티티의 필드명으로 정렬
            }
        });

        // API 응답(Page 객체)에 따라 상태 업데이트
        orders.value = response.data.content;
        totalElements.value = response.data.totalElements;
        totalPages.value = response.data.totalPages;
        
    } catch (err) {
        console.error('주문 내역 정보를 가져오는 데 실패했습니다:', err);
        error.value = '조회 실패';
        if (err.response?.status === 401) {
            alert('세션이 만료되었습니다. 다시 로그인해주세요.');
            authStore.logout(router);
        }
    } finally {
        isLoading.value = false;
    }
};

// --- 페이지 변경 함수 ---
const changePage = (page) => {
  // 요청하려는 페이지가 유효한 범위 내에 있는지 확인
  if (page >= 0 && page < totalPages.value) {
    currentPage.value = page; // 현재 페이지 상태 업데이트
    fetchOrderHistory(); // 해당 페이지 데이터 다시 요청
  }
};

// 백엔드로부터 사용자 프로필 정보를 가져오는 함수
const fetchUserProfile = async () => {
  try {
    const response = await api.get('/mypage/profile');
    originalUser.value = response.data;
    resetProfileForm();
  } catch (error) {
    console.error('프로필 정보를 가져오는 데 실패했습니다:', error);
    if (error.response?.status === 401) {
      alert('세션이 만료되었습니다. 다시 로그인해주세요.');
      authStore.logout(router);
    } else {
      alert('사용자 정보를 불러오는 중 문제가 발생했습니다.');
    }
  }
};

// Daum 우편번호 찾기 함수
const findAddress = () => {
  // Daum Postcode 스크립트가 로드되었는지 확인
  if (typeof daum === 'undefined' || !daum.Postcode) {
    console.error('Daum Postcode Script가 로드되지 않았습니다.');
    alert('주소 찾기 서비스를 이용할 수 없습니다.');
    return;
  }

  new daum.Postcode({
    oncomplete: function (data) {
      user.value.zipCode = data.zonecode;
      user.value.baseAddress = data.roadAddress;
      // 상세 주소 입력 필드에 자동으로 포커스
      document.querySelector('input[v-model="user.detailAddress"]')?.focus();
    }
  }).open();
};

// 프로필 저장 함수
const saveProfile = async () => {
  const payload = {
    name: user.value.name,
    birth: user.value.birth,
    gender: user.value.gender?.substring(0, 1),
    phone: user.value.phone.replace(/[^0-9]/g, ''), // 전화번호는 숫자만
    temperatureSensitivity: user.value.temperatureSensitivity,
    address: {
      zipCode: user.value.zipCode,
      base: user.value.baseAddress,
      detail: user.value.detailAddress
    }
  };
  console.log('Saving profile with payload:', payload);
  try {
    await api.put('/mypage/profile', payload);
    // 저장이 성공하면 originalUser를 업데이트
    originalUser.value = { ...user.value };
    alert('프로필이 성공적으로 저장되었습니다!');
    activeTab.value = 'orders'; // 저장 후 주문 내역 탭으로 이동
  } catch (error) {
    console.error('프로필 저장에 실패했습니다:', error);
    alert('프로필 저장 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
};

const handleCancel = () => {
  resetProfileForm(); // 폼을 원본 상태로 되돌립니다.
  activeTab.value = 'orders'; // 주문 내역 탭으로 이동합니다.
};

// 탭이 변경될 때마다 특정 로직 실행
watch(activeTab, (newTab) => {
  if (newTab === 'profile') {
    resetProfileForm();
  } else if (newTab === 'orders') {
    currentPage.value = 0; // 주문 탭으로 돌아올 때 항상 첫 페이지부터 보여주도록 초기화
    fetchOrderHistory();
  }
});

// 컴포넌트가 마운트될 때 사용자 정보를 자동으로 가져옵니다.
onMounted(() => {
  fetchOrderHistory(); 
  fetchUserProfile();
});
</script>