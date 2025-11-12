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
const isDev = import.meta.env.DEV;
</script>