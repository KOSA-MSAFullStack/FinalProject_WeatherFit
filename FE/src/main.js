import { createApp } from "vue";
import "./style.css";
import App from "./App.vue";

// 라우터 import
import router from "./router";
// Pinia import
import { createPinia } from 'pinia';
// Tanstack Query import
import { VueQueryPlugin, QueryClient } from "@tanstack/vue-query";

const pinia = createPinia();

const app = createApp(App);

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000,
      refetchOnWindowFocus: false,
    },
  },
});

app.use(pinia);
app.use(router);
app.use(VueQueryPlugin, { queryClient });
app.mount("#app");