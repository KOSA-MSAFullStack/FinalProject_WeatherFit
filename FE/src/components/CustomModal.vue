<template>
  <div v-if="isOpen" @click.self="$emit('close')" class="fixed inset-0 bg-gray-900 bg-opacity-75 flex items-center justify-center z-50 p-4 transition-opacity duration-300 ease-out">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-sm transform transition-all duration-300 scale-100">
      
      <div class="p-5 border-b border-gray-100">
        <h3 class="text-xl font-bold text-gray-800">{{ title }}</h3>
      </div>
      
      <div class="p-6 text-center">
        <p class="text-gray-700 whitespace-pre-wrap">{{ message }}</p>
      </div>
      
      <div class="p-5 border-t border-gray-100 flex gap-3 justify-end">
        <button v-if="isConfirm" 
          @click="$emit('close')" 
          class="px-5 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors font-medium"
        >
          취소
        </button>
        <button 
          @click="$emit('confirm')" 
          :class="['px-5 py-2 rounded-lg font-bold transition-colors', isConfirm ? 'bg-red-500 text-white hover:bg-red-600' : 'bg-blue-600 text-white hover:bg-blue-700']"
        >
          확인
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
defineProps({
  isOpen: { type: Boolean, required: true },
  title: { type: String, default: '알림' },
  message: { type: String, default: '' },
  isConfirm: { type: Boolean, default: false }
});

defineEmits(['close', 'confirm']);
</script>