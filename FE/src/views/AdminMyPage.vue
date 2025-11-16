<!-- AdminMyPage.vue -->
<!-- 관리자 페이지 -->

<template>
  <ProductModal v-if="isProductModalVisible" :product-to-edit="selectedProduct" @close="isProductModalVisible = false" @submit="handleProductSubmit" @delete="handleProductDelete" />
  <CategoryModal v-if="isCategoryModalVisible" @close="isCategoryModalVisible = false" @save="handleCategorySave" />
  <main class="main-wrap">
    <div class="grid-layout">
      <aside class="sidebar">
        <h3>관리자 메뉴</h3>
        <div class="menu-item" :class="{ active: activePage === 'sales' }" @click="showPage('sales')">판매 내역</div>
        <div class="menu-item" :class="{ active: activePage === 'products' }" @click="showPage('products')">상품 관리</div>
      </aside>

      <div style="width: 100%;">
        <!-- 판매 내역 -->
        <div id="sales" class="page" :class="{ active: activePage === 'sales' }">
          <div class="panel">
            <h2>판매 내역 💰</h2>

            <div class="filter-bar">
              <select>
                <option>오늘</option>
                <option>최근 7일</option>
                <option selected>최근 30일</option>
                <option>최근 3개월</option>
                <option>전체</option>
              </select>
              <input type="text" placeholder="주문번호/고객명 검색" style="flex:1; min-width:200px">
              <button class="btn">검색</button>
            </div>

            <div class="stats-grid" style="margin-bottom:20px">
              <div class="stat-box">
                <div class="stat-value">1,847</div>
                <div class="stat-label">총 주문 수</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">89,230,000원</div>
                <div class="stat-label">총 판매액</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">48,350원</div>
                <div class="stat-label">평균 주문액</div>
              </div>
            </div>

            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>주문번호</th>
                    <th>주문일시</th>
                    <th>상품정보</th>
                    <th>고객</th>
                    <th>수량</th>
                    <th>판매금액</th>
                  </tr>
                </thead>
                <tbody id="salesTable">
                  <tr v-for="sale in salesData" :key="sale.orderId">
                    <td><span class="order-id" @click="alert('주문 상세: ' + sale.orderId)">{{ sale.orderId }}</span></td>
                    <td>{{ sale.date }}</td>
                    <td>
                      <div class="product-info">
                        <div>
                          <div class="product-name">{{ sale.product }}</div>
                        </div>
                      </div>
                    </td>
                    <td>{{ sale.customer }}</td>
                    <td>{{ sale.qty }}개</td>
                    <td>{{ sale.price.toLocaleString() }}원</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <!-- 상품 관리 -->
        <div id="products" class="page" :class="{ active: activePage === 'products' }">
          <div class="panel">
            <h2>상품 관리 📦</h2>

            <div class="filter-bar">
              <button class="btn" @click="openRegisterProduct()">상품 등록</button>
              <button class="btn" @click="openCategoryModal()">카테고리 관리</button>
              <div style="flex:1"></div>
              <input type="text" placeholder="상품명 검색" style="width:250px">
              <button class="btn">검색</button>
            </div>

            <div class="stats-grid" style="margin-bottom:20px">
              <div class="stat-box">
                <div class="stat-value">{{ products.length }}</div>
                <div class="stat-label">등록 상품</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">{{ sellingProductsCount }}</div>
                <div class="stat-label">판매 중</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">{{ soldOutProductsCount }}</div>
                <div class="stat-label">품절</div>
              </div>
            </div>

            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>상품코드</th>
                    <th>상품명</th>
                    <th>카테고리</th>
                    <th>판매가</th>
                    <th>재고 수량</th>
                    <th>등록일</th>
                    <th>관리</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="product in products" :key="product.itemId">
                    <td>{{ product.itemCode }}</td>
                    <td>
                      <div class="product-info">
                        <div class="product-name">{{ product.itemName }}</div>
                      </div>
                    </td>
                    <td>{{ product.category }}</td>
                    <td>{{ product.price ? product.price.toLocaleString() : '0' }}원</td>
                    <td>{{ product.quantity }}개</td>
                    <td>{{ product.createdAt }}</td>
                    <td><button class="btn small" @click="openEditModal(product)">수정</button></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import ProductModal from '../components/ProductModal.vue';
import CategoryModal from '../components/CategoryModal.vue';
import api from '@/utils/axios';

export default {
  name: 'AdminMyPage',
  components: {
    ProductModal,
    CategoryModal,
  },
  data() {
    return {
      activePage: 'sales',
      isProductModalVisible: false,
      isCategoryModalVisible: false,
      selectedProduct: null,
      salesData: [
        { orderId: '20251103-0125', date: '2025.11.03 14:23', product: '울 블렌드 니트', customer: '김철수', qty: 1, price: 435000 },
        { orderId: '20251103-0124', date: '2025.11.03 13:45', product: '라이트 트렌치', customer: '이영희', qty: 1, price: 129000 },
        { orderId: '20251103-0123', date: '2025.11.03 12:30', product: '옥스포드 셔츠', customer: '박민수', qty: 2, price: 98000 },
        { orderId: '20251102-0456', date: '2025.11.02 18:20', product: '치노 팬츠', customer: '정수진', qty: 1, price: 59000 },
        { orderId: '20251102-0455', date: '2025.11.02 16:15', product: '방수 스니커즈', customer: '최지훈', qty: 1, price: 79000 },
        { orderId: '20251102-0454', date: '2025.11.02 14:50', product: '니트 풀오버', customer: '강민지', qty: 2, price: 138000 },
        { orderId: '20251101-0789', date: '2025.11.01 20:30', product: '울 코트', customer: '송하늘', qty: 1, price: 289000 },
        { orderId: '20251101-0788', date: '2025.11.01 19:15', product: '레더 재킷', customer: '윤서아', qty: 1, price: 459000 },
      ],
      products: []
    };
  },

  computed: {
    sellingProductsCount() {
      return this.products.filter(product => product.quantity > 0).length;
    },
    soldOutProductsCount() {
      return this.products.filter(product => product.quantity === 0).length;
    }
  },
  methods: {
    showPage(pageId) {
      this.activePage = pageId;
    },
    openRegisterProduct() {
      this.selectedProduct = null;
      this.isProductModalVisible = true;
    },
    openEditModal(product) {
      this.selectedProduct = product;
      this.isProductModalVisible = true;
    },
    openCategoryModal() {
      this.isCategoryModalVisible = true;
    },
    handleCategorySave(categoryData) {
      console.log('저장된 카테고리 데이터:', categoryData);
      // TODO: 백엔드 API 연결 시 카테고리 저장 로직 추가
    },

    // 상품 등록/수정 핸들러
    async handleProductSubmit(productData) {
      try {
        if (this.selectedProduct) {
          // 상품 수정
          const formData = new FormData();
          formData.append('itemName', productData.itemName);
          formData.append('price', productData.price);
          formData.append('quantity', productData.quantity);
          formData.append('gender', productData.gender);
          formData.append('category', productData.category);
          formData.append('itemCode', productData.itemCode);
          formData.append('aiDescription', productData.aiDescription || '');
          
          if (productData.maxTemperature !== null) {
            formData.append('maxTemperature', productData.maxTemperature);
          }
          if (productData.minTemperature !== null) {
            formData.append('minTemperature', productData.minTemperature);
          }
          
          if (productData.seasonName) {
            productData.seasonName.forEach(season => {
              formData.append('seasonName', season);
            });
          }

          if (productData.image) {
            formData.append('image', productData.image);
          }

          await api.patch(`/admin/items/${this.selectedProduct.itemId}`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          alert('상품이 성공적으로 수정되었습니다.');

        } else {
          // 상품 등록
          const formData = new FormData();
          formData.append('itemName', productData.itemName);
          formData.append('price', productData.price);
          formData.append('quantity', productData.quantity);
          formData.append('gender', productData.gender);
          formData.append('category', productData.category);
          formData.append('itemCode', productData.itemCode);
          formData.append('aiDescription', productData.aiDescription);
          
          if (productData.maxTemperature !== null) {
            formData.append('maxTemperature', productData.maxTemperature);
          }
          if (productData.minTemperature !== null) {
            formData.append('minTemperature', productData.minTemperature);
          }
          
          if (productData.seasonName) {
            productData.seasonName.forEach(season => {
              formData.append('seasonName', season);
            });
          }

          if (productData.image) {
            formData.append('image', productData.image);
          }

          await api.post('/admin/items', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          alert('상품이 성공적으로 등록되었습니다.');
        }
        this.isProductModalVisible = false;
        this.fetchProducts();
      } catch (error) {
        console.error('상품 처리 실패:', error);
        alert('상품 처리 중 오류가 발생했습니다: ' + (error.response?.data?.error || error.message));
      }
    },

    // 상품 삭제 핸들러
    async handleProductDelete(itemId) {
      try {
        await api.delete(`/admin/items/${itemId}`);
        alert('상품이 성공적으로 삭제되었습니다.');
        this.isProductModalVisible = false;
        this.fetchProducts();
      } catch (error) {
        console.error('상품 삭제 실패:', error);
        alert('상품 삭제에 실패했습니다: ' + (error.response?.data?.error || error.message));
      }
    },
    async fetchProducts() {
      try {
        const response = await api.get('/api/items');
        this.products = response.data;
      } catch (error) {
        console.error('상품 목록을 불러오는 데 실패했습니다:', error);
        alert('상품 목록을 불러오는 데 실패했습니다.');
      }
    }
  },
  mounted() {
    this.fetchProducts();
  }
};
</script>

<style scoped>
.main-wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  min-height: 100vh;
}

.grid-layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 20px;
  margin-top: 16px;
  align-items: start;
  height: 100%;
}

@media(max-width:900px) {
  .grid-layout {
    grid-template-columns: 1fr
  }
}

.sidebar {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 16px;
  height: fit-content;
  position: sticky;
  top: 16px;
}

.sidebar h3 {
  margin: 0 0 12px 0;
  font-size: 16px
}

.menu-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background .2s;
  margin-bottom: 4px
}

.menu-item:hover {
  background: var(--chip)
}

.menu-item.active {
  background: linear-gradient(135deg, #4f9cf9, #a78bfa);
  color: #ffffff;
  font-weight: 700
}

.panel {
  background: var(--panel);
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, .04);
  margin-bottom: 16px;
  width: 100%;
}

h2 {
  margin: 0 0 20px 0;
  font-size: 22px;
  font-weight: 700
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px
}

@media(max-width:768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr)
  }
}

.stat-box {
  background: #ffffff;
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 16px;
  text-align: center
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 4px
}

.stat-label {
  font-size: 13px;
  color: var(--muted)
}

.btn {
  background: linear-gradient(135deg, #4f9cf9, #a78bfa);
  color: #ffffff;
  border: none;
  font-weight: 700;
  cursor: pointer;
  transition: opacity .2s;
  border-radius: 10px;
  padding: 10px 16px
}

.btn:hover {
  opacity: .9
}

.btn.ghost {
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
  color: #111827
}

.btn.small {
  padding: 6px 10px;
  font-size: 13px
}

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center
}

.filter-bar select,
.filter-bar input {
  background-color: #f9fafb;
  color: var(--text);
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 8px 12px;
  outline: none;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.table-container {
  overflow-x: auto
}

table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

th {
  background: #f9fafb;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid var(--line);
  font-size: 14px;
}

th:nth-child(1) { width: 15%; }
th:nth-child(2) { width: 25%; }
th:nth-child(3) { width: 15%; }
th:nth-child(4) { width: 13%; }
th:nth-child(5) { width: 9%; }
th:nth-child(6) { width: 12%; }
th:nth-child(7) { width: 8%; }

td {
  padding: 12px;
  border-bottom: 1px solid var(--line);
  vertical-align: middle;
}

td:nth-child(1),
td:nth-child(3) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

tr:hover {
  background: #f9fafb
}

.order-id {
  color: var(--accent);
  font-weight: 600;
  cursor: pointer
}

.order-id:hover {
  text-decoration: underline
}

.product-info {
  display: flex;
  gap: 10px;
  align-items: center
}

.product-name {
  font-weight: 600;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.page {
  display: none;
  min-height: 800px;
}

.page.active {
  display: block
}
</style>
