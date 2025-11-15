import axios from "axios";

// 오늘 날씨 정보를 불러오는 API
export async function getTodayWeather(region) {
    const res = await axios.get("http://localhost:8080/weather/today", {
        params: { region }, // 파라미터 : 주소
    })
    return res.data;
}