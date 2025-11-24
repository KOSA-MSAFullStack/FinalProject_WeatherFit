<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" @click.self="close">
    <div class="bg-white w-full max-w-[550px] rounded-2xl shadow-xl flex flex-col max-h-[90vh] overflow-hidden animate-fade-in-up">
      
      <!-- 모달 헤더 -->
      <div class="p-4 border-b border-gray-100 flex justify-between items-center">
        <h2 class="text-xl font-bold text-gray-900">
          {{ isEditMode ? '리뷰 수정' : '리뷰 작성' }}
        </h2>
        <button @click="close" class="text-gray-400 hover:text-gray-600 transition-colors">
          <!-- 수정: Lucide 컴포넌트 사용 -->
          <X :size="24" />
        </button>
      </div>

      <!-- 모달 바디 -->
      <div class="p-5 overflow-y-auto custom-scrollbar">
        
        <!-- 상품 정보 -->
        <div class="bg-gray-50 p-3 rounded-xl mb-5 border border-gray-100">
          <p class="font-semibold text-gray-800 text-sm">{{ orderItem.itemName }}</p>
        </div>

        <!-- 별점 입력 -->
        <div class="mb-6">
          <label class="block text-sm font-bold text-gray-900 mb-2">별점</label>
          <div 
            class="flex gap-1" 
            @mouseleave="resetHover"
          >
            <div 
              v-for="index in 5" 
              :key="index"
              class="relative w-8 h-8 cursor-pointer transition-transform hover:scale-110"
              @mousemove="handleMouseMove($event, index)"
              @click="setScore(index)"
            >
              <!-- 수정: Lucide Star 컴포넌트 사용 -->
              <!-- 배경 별 (회색) -->
              <Star class="absolute top-0 left-0 w-full h-full text-gray-200 fill-current" />
              
              <!-- 전경 별 (노란색) - clip-path로 제어 -->
              <Star
                class="absolute top-0 left-0 w-full h-full text-yellow-400 fill-current transition-all duration-100 ease-in-out"
                :style="{ clipPath: getStarClipPath(index) }"
              />
            </div>
          </div>
          <p class="text-sm font-medium text-yellow-500 mt-1 h-5">{{ displayScore > 0 ? displayScore + '점' : '' }}</p>
        </div>

        <!-- 라디오 버튼 그룹 (이하 동일) -->
        <div class="mb-6">
          <label class="block text-sm font-bold text-gray-900 mb-2">착용한 날의 날씨</label>
          <div class="flex flex-wrap gap-2">
            <label 
              v-for="opt in weatherOptions" 
              :key="opt"
              :class="[
                'px-3 py-2 rounded-xl border text-sm cursor-pointer transition-all duration-200',
                form.weather === opt 
                  ? 'bg-indigo-50 border-indigo-400 text-indigo-700 font-semibold' 
                  : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'
              ]"
            >
              <input type="radio" :value="opt" v-model="form.weather" class="hidden">
              {{ opt }}
            </label>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-bold text-gray-900 mb-2">날씨 체감</label>
          <div class="flex flex-wrap gap-2">
            <label 
              v-for="opt in weatherSuitabilityOptions" 
              :key="opt"
              :class="[
                'px-3 py-2 rounded-xl border text-sm cursor-pointer transition-all duration-200',
                form.weatherSuitability === opt 
                  ? 'bg-indigo-50 border-indigo-400 text-indigo-700 font-semibold' 
                  : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'
              ]"
            >
              <input type="radio" :value="opt" v-model="form.weatherSuitability" class="hidden">
              {{ opt }}
            </label>
          </div>
        </div>

        <div class="mb-6">
          <label class="block text-sm font-bold text-gray-900 mb-2">실내 착용감</label>
          <div class="flex flex-wrap gap-2">
            <label 
              v-for="opt in breathabilityOptions" 
              :key="opt"
              :class="[
                'px-3 py-2 rounded-xl border text-sm cursor-pointer transition-all duration-200',
                form.breathability === opt 
                  ? 'bg-indigo-50 border-indigo-400 text-indigo-700 font-semibold' 
                  : 'bg-white border-gray-200 text-gray-600 hover:bg-gray-50'
              ]"
            >
              <input type="radio" :value="opt" v-model="form.breathability" class="hidden">
              {{ opt }}
            </label>
          </div>
        </div>

        <div class="mb-2">
          <label class="block text-sm font-bold text-gray-900 mb-2">리뷰 내용</label>
          <textarea 
            v-model="form.content"
            class="w-full min-h-[120px] p-3 border border-gray-200 rounded-xl resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent text-sm placeholder-gray-400"
            placeholder="상품에 대한 솔직한 리뷰를 남겨주세요. (예시: 실내에서 딱 좋았어요)"
          ></textarea>
        </div>

      </div>

      <!-- 모달 푸터 (이하 동일) -->
      <div class="p-4 border-t border-gray-100 flex justify-between items-center bg-gray-50/50">
        <div>
          <button 
            v-if="isEditMode" 
            @click="$emit('delete')"
            class="px-4 py-2.5 rounded-xl text-sm font-bold text-white bg-red-500 hover:bg-red-600 transition-colors cursor-pointer"
          >
            삭제하기
          </button>
        </div>
        <div class="flex gap-2">
          <button 
            @click="close"
            class="px-4 py-2.5 rounded-xl text-sm font-bold text-gray-700 bg-white border border-gray-200 hover:bg-gray-50 transition-colors cursor-pointer"
          >
            취소
          </button>
          <button 
            @click="submit"
            :disabled="!isValid"
            :class="[
              'px-4 py-2.5 rounded-xl text-sm font-bold text-white transition-all shadow-md cursor-pointer',
              isValid 
                ? 'bg-gradient-to-br from-blue-400 to-violet-400 hover:opacity-90' 
                : 'bg-gray-300 cursor-not-allowed shadow-none'
            ]"
          >
            {{ isEditMode ? '수정하기' : '등록하기' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
// 수정: Lucide 아이콘 컴포넌트 import
import { Star, X } from 'lucide-vue-next';

// 이하 <script setup> 내용은 이전과 완전히 동일합니다.
const props = defineProps({
  isOpen: Boolean,
  orderItem: {
    type: Object,
    required: true,
    default: () => ({ itemName: ''})
  },
  existingReview: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'submit', 'delete']);

const weatherOptions = ['맑음', '흐림', '강풍', '비', '눈'];
const weatherSuitabilityOptions = ['추워요', '시원해요', '보통이에요', '따뜻해요', '더워요'];
const breathabilityOptions = ['편해요', '보통이에요', '답답해요'];

const form = ref({
  score: 0,
  weather: '',
  weatherSuitability: '',
  breathability: '',
  content: ''
});

const hoverScore = ref(0);
const isEditMode = computed(() => !!props.existingReview);
const displayScore = computed(() => hoverScore.value > 0 ? hoverScore.value : form.value.score);

const isValid = computed(() => {
  return form.value.score > 0 && 
         form.value.weather && 
         form.value.weatherSuitability && 
         form.value.breathability && 
         form.value.content.trim().length > 0;
});

const handleMouseMove = (e, index) => {
  const rect = e.currentTarget.getBoundingClientRect();
  const isHalf = (e.clientX - rect.left) < (rect.width / 2);
  hoverScore.value = index - (isHalf ? 0.5 : 0);
};

const resetHover = () => {
  hoverScore.value = 0;
};

const setScore = (index) => {
  if (hoverScore.value > 0) {
    form.value.score = hoverScore.value;
  }
};

const getStarClipPath = (index) => {
  const score = displayScore.value;
  if (score >= index) return 'inset(0 0 0 0)';
  if (score === index - 0.5) return 'inset(0 50% 0 0)';
  return 'inset(0 100% 0 0)';
};

const close = () => emit('close');
const submit = () => { if (isValid.value) emit('submit', { ...form.value }); };

watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    if (props.existingReview) {
      form.value = { ...props.existingReview };
    } else {
      form.value = { score: 0, weather: '맑음', weatherSuitability: '보통이에요', breathability: '편해요', content: '' };
    }
    hoverScore.value = 0;
  }
});
</script>

<style scoped>
/* 이전과 완전히 동일합니다. */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: #f1f1f1; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #d1d5db; border-radius: 3px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #9ca3af; }

@keyframes fade-in-up {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.animate-fade-in-up {
  animation: fade-in-up 0.3s ease-out;
}
</style>