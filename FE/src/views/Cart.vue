<template>
  <div class="bg-gray-50 min-h-screen">
    
    <main class="max-w-6xl mx-auto py-8 px-4">
      
      <h2 class="text-3xl font-bold mb-6 text-gray-800">
        장바구니
        <span class="text-gray-400">({{ cart.length }})</span>
      </h2>

      <div class="grid grid-cols-1 md:grid-cols-[1fr_340px] gap-8">
        
        <div>
          <div class="bg-white border border-gray-200 rounded-lg shadow-sm min-h-[300px]">
            
            <div v-if="cart.length > 0">
              <div class="flex items-center gap-2 p-4 border-b border-gray-200">
                <button @click="toggleSelectAll(true)" class="text-sm font-medium text-gray-700 hover:text-black">전체 선택</button>
                <span class="text-gray-300">|</span>
                <button @click="toggleSelectAll(false)" class="text-sm font-medium text-gray-700 hover:text-black">선택 해제</button>
                <button @click="removeSelected" class="ml-auto bg-red-500 text-white text-xs font-medium px-3 py-1.5 rounded-md hover:bg-red-600">선택 삭제</button>
              </div>

              <div class="divide-y divide-gray-200">
                <div v-for="item in cart" :key="item.cartId" class="p-4 flex justify-between items-start">
                  
                  <div class="flex items-start gap-4">
                    <input 
                      type="checkbox" 
                      v-model="item.selected" 
                      class="w-5 h-5 cursor-pointer rounded accent-blue-600 shrink-0 mt-1"
                    >
                    <img 
                        :src="getFullImageUrl(item.itemImage)" 
                        :alt="item.itemName" 
                        class="w-20 h-20 object-cover rounded-lg border border-gray-200"
                        onerror="this.onerror=null;this.src='https://placehold.co/80x80/f1f5f9/94a3b8?text=Img';"
                    >
                    <div>
                      <p class="font-semibold text-base text-gray-800">{{ item.itemName }}</p>
                      <p class="text-sm text-gray-500 mt-1">단가: {{ formatPrice(item.itemPrice) }}원</p>
                    </div>
                  </div>

                  <div class="flex flex-col items-end gap-2 shrink-0 ml-4 w-32">
                    <p class="font-bold text-lg text-gray-800 text-right">{{ formatPrice(item.totalPrice) }}원</p>
                    
                    <div class="flex items-center rounded-md border border-gray-300">
                      <button 
                        @click="changeQuantity(item, -1)" 
                        :disabled="item.quantity <= 1 || item.isUpdating"
                        class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 font-semibold text-xl disabled:opacity-50 disabled:cursor-not-allowed transition"
                      >-</button>
                      
                      <span class="w-10 h-8 flex items-center justify-center text-center font-semibold border-x border-gray-300">
                        {{ item.quantity }}
                      </span>

                      <button 
                        @click="changeQuantity(item, 1)" 
                        :disabled="item.isUpdating"
                        class="w-8 h-8 flex items-center justify-center text-gray-500 hover:bg-gray-100 font-semibold text-xl disabled:opacity-50 disabled:cursor-not-allowed transition"
                      >+</button>
                    </div>
                    
                    <button @click="confirmRemove(item)" class="bg-red-500 text-white text-xs font-semibold rounded-md py-1.5 w-full max-w-[76px] hover:bg-red-600">삭제</button>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-else class="text-center py-20">
              <p class="text-gray-500 mb-5">장바구니가 비어 있습니다.</p>
              <button @click="continueShopping" class="bg-gray-800 text-white hover:bg-gray-700 px-6 py-2.5 rounded-lg font-semibold">쇼핑 계속하기</button>
            </div>

            <div v-if="isLoading" class="absolute inset-0 bg-white bg-opacity-70 flex items-center justify-center rounded-lg">
                <svg class="animate-spin h-8 w-8 text-blue-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
            </div>

          </div>
        </div>

        <div>
          <div class="bg-white border border-gray-200 rounded-lg shadow-sm sticky top-8">
            
            <h3 class="text-lg font-semibold text-gray-800 p-6 border-b border-gray-200">주문 요약</h3>
            
            <div class="p-6 space-y-4">
              <div class="flex justify-between items-baseline text-lg font-bold">
                <span class="text-gray-800">결제 예정 금액</span>
                <span>{{ formatPrice(totalPrice) }}원</span>
              </div>
              
              <button @click="checkout" 
                class="w-full mt-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white hover:opacity-90 py-3 rounded-lg font-bold text-base shadow-md transition-all disabled:opacity-60"
                :disabled="selectedCart.length === 0"
              >
                주문하기 ({{ selectedCart.length }}개)
              </button>
              
              <button @click="continueShopping" 
                class="w-full mt-2 bg-white border border-gray-300 text-gray-800 hover:bg-gray-50 py-3 rounded-lg font-bold text-base transition-all"
              >
                쇼핑 계속하기
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>

  <CustomModal 
    :isOpen="isModalOpen" 
    :title="modalTitle"
    :message="modalMessage" 
    :isConfirm="modalIsConfirm"
    @close="isModalOpen = false" 
    @confirm="handleModalConfirm"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
// API 호출을 위한 axios 인스턴스
import api from '@/utils/axios'; 
// 커스텀 모달 컴포넌트 (별도로 만들어야 함)
import CustomModal from '@/components/CustomModal.vue'; 

const router = useRouter();

// 장바구니 상태
const cart = ref([]);
const isLoading = ref(false);

// 모달 상태
const isModalOpen = ref(false);
const modalTitle = ref('');
const modalMessage = ref('');
const modalIsConfirm = ref(false);
const modalCallback = ref(null);

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

// 헬퍼: 숫자 포매팅
const formatPrice = (value) => {
  if (typeof value !== 'number' || isNaN(value)) return '0';
  return value.toLocaleString();
};

// ----------------------------------------------------
// 1. API 호출 로직
// ----------------------------------------------------

// 장바구니 항목 조회
const fetchCartItems = async () => {
    isLoading.value = true;
    try {
        // GET /carts API 호출
        const response = await api.get('/carts');
        // 응답 데이터를 CartItemResponse 형식에 맞게 변환하여 저장
        cart.value = response.data.map(item => ({
            ...item,
            selected: true, // 기본적으로 모두 선택
            isUpdating: false, // 수량 변경 중 상태
        }));
    } catch (error) {
        console.error("장바구니 목록 조회 실패:", error);
        alert('장바구니 목록을 불러오는 데 실패했습니다.');
    } finally {
        isLoading.value = false;
    }
};

// 수량 변경 API 호출
const updateQuantityApi = async (item, newQuantity) => {
    if (newQuantity < 1) return;

    item.isUpdating = true; // 로딩 상태 설정
    try {
        // PUT /carts/{cartId} API 호출
        await api.put(`/carts/${item.cartId}`, { quantity: newQuantity });
        
        // 성공 시 로컬 상태 업데이트
        item.quantity = newQuantity;
        item.totalPrice = item.itemPrice * newQuantity;

    } catch (error) {
        console.error("수량 변경 실패:", error);
        alert('수량 변경에 실패했습니다. (유효성 오류 확인)');
        // 실패 시 이전 수량으로 되돌리거나 목록 재조회
        fetchCartItems(); 
    } finally {
        item.isUpdating = false;
    }
};

// 항목 삭제 API 호출
const removeCartItemApi = async (cartId) => {
    isLoading.value = true;
    try {
        // DELETE /carts/{cartId} API 호출
        await api.delete(`/carts/${cartId}`);
        // 로컬 상태에서 해당 항목 제거
        cart.value = cart.value.filter(item => item.cartId !== cartId);
        showCustomModal("삭제 완료", "장바구니에서 상품이 삭제되었습니다.", false);
    } catch (error) {
        console.error("상품 삭제 실패:", error);
        showCustomModal("삭제 실패", "상품 삭제에 실패했습니다.", false);
    } finally {
        isLoading.value = false;
    }
};

// 선택 항목 삭제 API 호출 (이 예시에서는 개별 삭제만 구현하며, 선택 삭제는 서버 로직에 따라 구현해야 함)
const removeSelectedApi = async (cartIds) => {
    isLoading.value = true;
    try {
        // 여러 항목을 한 번에 삭제하는 API가 없다고 가정하고, 순차적으로 호출하거나, 
        // 혹은 DELETE /carts/selected 같은 새로운 API를 만들어야 합니다.
        for (const cartId of cartIds) {
            await api.delete(`/carts/${cartId}`);
        }
        await fetchCartItems(); // 전체 목록 새로고침
        showCustomModal("선택 삭제 완료", `${cartIds.length}개의 상품이 삭제되었습니다.`, false);
    } catch (error) {
        console.error("선택 삭제 실패:", error);
        showCustomModal("삭제 실패", "선택된 상품 삭제에 실패했습니다.", false);
    } finally {
        isLoading.value = false;
    }
};

// ----------------------------------------------------
// 2. UI 상호작용 로직
// ----------------------------------------------------

// 수량 변경 핸들러
const changeQuantity = (item, delta) => {
    const newQuantity = item.quantity + delta;
    if (newQuantity >= 1) {
        updateQuantityApi(item, newQuantity);
    }
};

// 전체 선택/해제
const toggleSelectAll = (select) => {
    cart.value.forEach(item => item.selected = select);
};

// 개별 삭제 확인 모달
const confirmRemove = (item) => {
    modalTitle.value = "상품 삭제 확인";
    modalMessage.value = `'${item.itemName}'을(를) 장바구니에서 삭제하시겠습니까?`;
    modalIsConfirm.value = true;
    modalCallback.value = () => removeCartItemApi(item.cartId);
    isModalOpen.value = true;
};

// 선택 항목 삭제
const removeSelected = () => {
    const selectedIds = selectedCart.value.map(item => item.cartId);
    if (selectedIds.length === 0) {
        showCustomModal("알림", "삭제할 상품을 선택해주세요.", false);
        return;
    }

    modalTitle.value = "선택 삭제 확인";
    modalMessage.value = `선택된 상품 ${selectedIds.length}개를 장바구니에서 모두 삭제하시겠습니까?`;
    modalIsConfirm.value = true;
    modalCallback.value = () => removeSelectedApi(selectedIds);
    isModalOpen.value = true;
};

// 주문하기
const checkout = () => {
    const itemsToOrder = selectedCart.value;
    if (itemsToOrder.length === 0) {
        showCustomModal("알림", "주문할 상품을 선택해주세요.", false);
        return;
    }
    
    // 실제 주문 페이지로 이동 또는 주문 API 호출 로직
    showCustomModal("주문 진행", `${itemsToOrder.length}개 상품 주문을 진행합니다. (구현 필요)`, false);
    // router.push('/checkout'); 
};

// 쇼핑 계속하기
const continueShopping = () => {
    router.push('/main'); // 메인 페이지 또는 스토어 페이지로 이동
};

// 커스텀 모달 표시 헬퍼
const showCustomModal = (title, message, isConfirm, callback = null) => {
    modalTitle.value = title;
    modalMessage.value = message;
    modalIsConfirm.value = isConfirm;
    modalCallback.value = callback;
    isModalOpen.value = true;
};

// 모달 확인 버튼 클릭 핸들러
const handleModalConfirm = () => {
    if (modalCallback.value) {
        modalCallback.value();
    }
    isModalOpen.value = false;
    modalCallback.value = null;
};

// ----------------------------------------------------
// 3. Computed Properties
// ----------------------------------------------------

// 선택된 장바구니 항목
const selectedCart = computed(() => {
    return cart.value.filter(item => item.selected);
});

// 총 결제 예정 금액 (선택된 항목만)
const totalPrice = computed(() => {
    return selectedCart.value.reduce((sum, item) => sum + item.totalPrice, 0);
});

// ----------------------------------------------------
// 4. Lifecycle Hooks
// ----------------------------------------------------

onMounted(() => {
    fetchCartItems();
});
</script>