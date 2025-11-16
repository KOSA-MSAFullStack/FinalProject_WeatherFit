// author : 김경아
import axios from "@/utils/axios";

// itemId로 상품 상세 불러오는 API
export async function getItemDetail(itemId) {
    const res = await axios.get(`http://localhost:8080/api/items/${itemId}`)
    return res.data;
}