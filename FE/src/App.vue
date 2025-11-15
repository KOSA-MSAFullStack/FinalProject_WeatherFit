<template>
  <div class="min-h-screen text-[#2B2B2B]">
    <RouterView />
    <!-- Vue Query Devtools: only visible in development -->
    <VueQueryDevtools v-if="isDev" :initialIsOpen="false" />
  </div>
</template>

<script setup>
import { RouterView } from "vue-router";
import { VueQueryDevtools } from "@tanstack/vue-query-devtools";
import { ref } from "vue";
import { provide } from "vue";

const isDev = import.meta.env.DEV;

// 모든 화면에서 공유할 region 상태
const region = ref('대전시') // 기본 주소값

// 하위 컴포넌트에서 inject 할 수 있도록 제공
provide('region', region)
</script>