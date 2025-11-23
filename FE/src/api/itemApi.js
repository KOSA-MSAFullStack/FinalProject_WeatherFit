// author : 김경아
import axios from "@/utils/axios";

// itemId로 상품 상세 불러오는 API
export async function getItemDetail(itemId) {
    const res = await axios.get(`http://localhost:8080/api/items/${itemId}`)
    return res.data;
}

// 상품 목록 조회 API
export async function getAllItem() {
    const res = await axios.get(`http://localhost:8080/api/items/all`)
    return res.data;
    // return res.data.content ?? [];
}

// 장바구니 담기 API
export async function addToCart(itemId) {
    const res = await axios.post(`http://localhost:8080/carts`, {
        itemId
    });
    return res;
}

// 카테고리 목록 조회 API
export const getCategoryData = async () => {
    const res = await axios.get('http://localhost:8080/api/categories') 
    return res.data
}