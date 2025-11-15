// author : 김경아
import axios from "@/utils/axios";

// 오늘 날씨 기반 옷 추천 받는 API
export async function getTodayRecommendation(region) {
    const res = await axios.get("http://localhost:8080/recommendations/today", {
        params: { region }, // 파라미터 : 주소
    })
    return res.data;
}

// 내일 날씨 기반 옷 추천 받는 API
export async function getTomorrowRecommendation(region) {
    const res = await axios.get("http://localhost:8080/recommendations/tomorrow", {
        params: { region }, // 파라미터 : 주소
    })
    return res.data;
}

// 이번주 날씨 기반 옷 추천 받는 API
export async function getWeeklyRecommendation(region) {
    const res = await axios.get("http://localhost:8080/recommendations/weekly", {
        params: { region }, // 파라미터 : 주소
    })
    return res.data;
}