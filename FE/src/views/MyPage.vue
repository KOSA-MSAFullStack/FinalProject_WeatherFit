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
                  <div class="text-2xl font-bold text-gray-900">12</div>
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
              <h2 class="text-xl font-bold mb-4">주문 내역</h2>
              <div class="space-y-3">
                <div v-for="order in orders" :key="order.id" class="bg-white border border-gray-200 rounded-lg p-4 flex items-center gap-4">
                  <div class="w-20 h-20 rounded-md bg-gray-100 shrink-0"></div>
                  <div class="grow">
                    <p class="font-bold text-gray-800">{{ order.name }}</p>
                    <p class="text-xs text-gray-500 mt-1">{{ order.date }} · 주문번호: {{ order.id }} · {{ order.price.toLocaleString() }}원</p>
                    <div class="mt-2 space-x-2">
                      <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-white text-gray-700 border border-gray-300 hover:bg-gray-50">상세보기</button>
                      <button class="px-3 py-1.5 rounded-md font-semibold text-xs transition-colors duration-200 bg-blue-500 text-white hover:bg-blue-600">리뷰 쓰기</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 기본 정보 페이지 -->
          <div v-if="activeTab === 'profile'">
            <div class="bg-white border border-gray-200 rounded-xl p-6 shadow-sm mb-6">
              <h2 class="text-xl font-bold mb-5">기본 정보</h2>
              <form @submit.prevent="saveProfile" class="space-y-4">
                <div>
                  <label for="name" class="block mb-1.5 text-sm font-medium text-gray-700">이름</label>
                  <input id="name" type="text" v-model="user.name" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div>
                  <label for="email" class="block mb-1.5 text-sm font-medium text-gray-700">이메일 (아이디)</label>
                  <input id="email" type="email" v-model="user.email" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-gray-100" readonly>
                </div>
                <div class="flex flex-col sm:flex-row gap-4">
                  <div class="flex-1">
                    <label for="birthdate" class="block mb-1.5 text-sm font-medium text-gray-700">생년월일</label>
                    <input id="birthdate" type="date" v-model="user.birthdate" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                  </div>
                  <div class="flex-1">
                    <label for="gender" class="block mb-1.5 text-sm font-medium text-gray-700">성별</label>
                    <select id="gender" v-model="user.gender" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                      <option>남성</option>
                      <option>여성</option>
                      <option>선택 안함</option>
                    </select>
                  </div>
                </div>
                <div>
                  <label for="phone" class="block mb-1.5 text-sm font-medium text-gray-700">연락처</label>
                  <input id="phone" type="tel" v-model="user.phone" class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                </div>
                <div class="flex justify-end space-x-2 mt-6">
                  <button type="button" @click="activeTab = 'orders'" class="px-4 py-2 rounded-md font-semibold text-sm transition-colors duration-200 bg-white text-gray-700 border border-gray-300 hover:bg-gray-50">취소</button>
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
    <footer class="max-w-7xl mx-auto py-5 px-4 mt-10 border-t border-gray-200 text-center text-xs text-gray-500">
      © WeatherFit Shop · 마이페이지
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 현재 활성화된 탭을 관리하는 상태
const activeTab = ref('orders');

// 실제로는 API로부터 받아올 사용자 데이터 (임시 데이터)
const user = ref({
  name: '김철수',
  email: 'chulsoo@example.com',
  birthdate: '1995-05-15',
  gender: '남성',
  phone: '010-1234-5678',
});

// 임시 주문 내역 데이터
const orders = ref([
  { id: '20251028-001234', name: '울 블렌드 인타르시아 니트 탑', date: '2025.10.28', price: 435000 },
  { id: '20251025-005678', name: '라이트 트렌치 코트', date: '2025.10.25', price: 129000 },
  { id: '20251020-002345', name: '옥스포드 셔츠 외 2건', date: '2025.10.20', price: 187000 },
]);

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

// 프로필 저장 함수 (임시)
const saveProfile = () => {
  alert('기본 정보가 저장되었습니다!');
  activeTab.value = 'orders'; // 저장 후 주문 내역 탭으로 이동
};
</script>