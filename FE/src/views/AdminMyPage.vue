<!-- AdminMyPage.vue -->
<!-- 관리자 페이지 -->
<!-- * author: 김기성 -->

<template>
  <ProductModal v-if="isProductModalVisible" :product-to-edit="selectedProduct" :category-data="categoryData"@close="isProductModalVisible = false" @submit="handleProductSubmit" @delete="handleProductDelete" />
  <CategoryModal v-if="isCategoryModalVisible" :category-data="categoryData" @close="isCategoryModalVisible = false" @save="handleCategorySave" />
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
              <input type="text" v-model="salesSearchKeyword" placeholder="주문번호/고객명 검색" style="flex:1; min-width:200px" @keyup.enter="searchSales">
              <button class="btn" @click="searchSales">검색</button>
            </div>

            <div class="stats-grid" style="margin-bottom:20px">
              <div class="stat-box">
                <div class="stat-value">{{ totalOrderCount }}</div>
                <div class="stat-label">총 주문 수</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">{{ totalSalesAmount.toLocaleString() }}원</div>
                <div class="stat-label">총 판매액</div>
              </div>
              <div class="stat-box">
                <div class="stat-value">{{ averageOrderAmount.toLocaleString() }}원</div>
                <div class="stat-label">평균 주문액</div>
              </div>
            </div>

            <div class="space-y-6">
              <div v-if="groupedSales.length === 0" class="empty-sales">
                판매 내역이 없습니다.
              </div>
              <div v-for="group in groupedSales" :key="group.orderId" class="sale-card">
                <div class="sale-card-header">
                  <div class="flex items-center gap-4">
                    <span>{{ formatDate(group.date) }} 주문</span>
                    <span class="order-no">| 주문번호: {{ group.orderId }}</span>
                  </div>
                  <div class="text-xs text-gray-500">고객: {{ group.customer }}</div>
                </div>
                <div v-for="sale in group.items" :key="sale.product" class="p-4 flex gap-4 items-center border-b border-gray-100 last:border-b-0">
                  <div class="sale-item-image">
                    <img :src="getFullImageUrl(sale.imageURL)" :alt="sale.product" class="w-full h-full object-cover">
                  </div>
                  <div class="grow">
                    <p class="font-bold text-gray-800 line-clamp-1">{{ sale.product }}</p>
                    <p class="text-xs text-gray-500 mt-1">수량: {{ sale.qty }}개</p>
                  </div>
                  <div class="text-sm font-semibold text-gray-700 w-24 text-right">{{ sale.price.toLocaleString() }}원</div>
                </div>
                <div class="sale-card-footer">
                  <span>총 {{ group.totalQty }}개</span>
                  <span class="font-bold">합계: {{ group.totalPrice.toLocaleString() }}원</span>
                </div>
              </div>
            </div>
            
            <!-- 판매 내역 페이지네이션 UI -->
            <div class="pagination-container">
              <div class="items-per-page">
                <label for="sales-items-per-page">페이지 당 항목 수:</label>
                <select id="sales-items-per-page" v-model="salesPageSize" @change="changeSalesPageSize">
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="20">20</option>
                </select>
              </div>
              <div class="pagination-controls">
                <button @click="goToSalesFirstPage" :disabled="salesCurrentPage === 0">«</button>
                <button @click="salesPrevPage" :disabled="salesCurrentPage === 0">‹</button>
                <span class="page-info">{{ salesCurrentPage + 1 }} / {{ salesTotalPages }}</span>
                <button @click="salesNextPage" :disabled="salesCurrentPage >= salesTotalPages - 1">›</button>
                <button @click="goToSalesLastPage" :disabled="salesCurrentPage >= salesTotalPages - 1">»</button>
              </div>
              <div class="page-jump">
                <input type="number" v-model.number="salesJumpToPage" @keyup.enter="goToSalesPage" min="1" :max="salesTotalPages">
                <button @click="goToSalesPage">이동</button>
              </div>
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
              <input type="text" v-model="searchKeyword" placeholder="상품명/상품 코드 검색" style="width:250px" @keyup.enter="searchProducts">
              <button class="btn" @click="searchProducts">검색</button>
            </div>

            <div class="stats-grid" style="margin-bottom:20px">
              <div class="stat-box">
                <div class="stat-value">{{ totalProducts }}</div>
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
            <!-- 페이지네이션 UI -->
            <div class="pagination-container">
              <div class="items-per-page">
                <label for="items-per-page">페이지 당 항목 수:</label>
                <select id="items-per-page" v-model="pageSize" @change="changePageSize">
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="30">30</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                </select>
              </div>
              <div class="pagination-controls">
                <button @click="goToFirstPage" :disabled="currentPage === 0">«</button>
                <button @click="prevPage" :disabled="currentPage === 0">‹</button>
                <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
                <button @click="nextPage" :disabled="currentPage >= totalPages - 1">›</button>
                <button @click="goToLastPage" :disabled="currentPage >= totalPages - 1">»</button>
              </div>
              <div class="page-jump">
                <input type="number" v-model.number="jumpToPage" @keyup.enter="goToPage" min="1" :max="totalPages">
                <button @click="goToPage">이동</button>
              </div>
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

const API_BASE_URL = api.defaults.baseURL || '';

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
      categoryData: {}, // 백엔드에서 불러올 카테고리 데이터
      searchKeyword: '', // 상품 검색 키워드
      salesSearchKeyword: '',    // 판매 내역 검색 키워드
      salesData: [], // 현재 페이지의 판매 데이터
      products: [],   // 현재 페이지의 상품 데이터
      totalProducts: 0, // 전체 상품 수
      sellingProductsCount: 0, // 판매 중인 상품 수
      soldOutProductsCount: 0, // 품절 상품 수
      // 상품 관리 페이징
      currentPage: 0,   // 현재 페이지 (0-based)
      pageSize: 10,      // 페이지당 상품 수
      totalPages: 0,     // 전체 페이지 수
      jumpToPage: 1,      // 페이지 이동 입력 모델
      // 판매 내역 페이징
      salesCurrentPage: 0,   // 현재 페이지 (0-based)
      salesPageSize: 5,      // 페이지당 항목 수
      salesTotalPages: 0,     // 전체 페이지 수
      salesTotalElements: 0,  // 전체 항목 수
      salesJumpToPage: 1      // 페이지 이동 입력 모델
    };
  },

  computed: {
    groupedSales() {
      const groups = this.salesData.reduce((acc, sale) => {
        if (!acc[sale.orderId]) {
          acc[sale.orderId] = {
            orderId: sale.orderId,
            date: sale.date,
            customer: sale.customer,
            items: [],
            totalPrice: 0,
            totalQty: 0,
          };
        }
        acc[sale.orderId].items.push(sale);
        acc[sale.orderId].totalPrice += sale.price;
        acc[sale.orderId].totalQty += sale.qty;
        return acc;
      }, {});
      return Object.values(groups).sort((a, b) => new Date(b.date) - new Date(a.date));
    },
    
    // 총 주문 수
    totalOrderCount() {
      // 그룹화된 판매 내역의 수를 기반으로 총 주문 수를 계산
      return this.groupedSales.length;
    },
    // 총 판매액
    totalSalesAmount() {
      return this.salesData.reduce((sum, sale) => sum + sale.price, 0);
    },
    // 평균 주문액
    averageOrderAmount() {
      if (this.groupedSales.length === 0) return 0;
      // 총 판매액을 총 주문 수로 나누어 평균 주문액을 계산
      return Math.round(this.totalSalesAmount / this.groupedSales.length);
    }
  },
  methods: {
    getFullImageUrl(relativePath) {
      if (!relativePath) {
        return 'https://placehold.co/80x80/f1f5f9/94a3b8?text=Img'; 
      }
      return `${API_BASE_URL}${relativePath}`;
    },
    formatDate(datetime) {
        if (!datetime) return '';
        const date = new Date(datetime);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        }).replace(/\. /g, '.').replace(/\.$/, '');
    },
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
    handleCategorySave(updatedCategoryData) {
      this.categoryData = updatedCategoryData;
      console.log('저장된 카테고리 데이터:', this.categoryData);
    },

    // [상품 등록/수정 핸들러]
    async handleProductSubmit(productData) {
      try {
        if (this.selectedProduct) {
          // [상품 수정]
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
          // [상품 등록]
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

    // [상품 삭제 핸들러]
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

    // [상품 목록 불러오기]
    async fetchProducts() {
      const keyword = this.searchKeyword.trim();
      
      // 검색 키워드가 있으면 검색 API, 없으면 전체 목록 API 호출
      if (keyword) {
        await this.searchProductsAPI(keyword);
      } else {
        await this.fetchProductsAPI();
      }
    },
    
    // [전체 상품 목록 API 호출]
    async fetchProductsAPI() {
      try {
        const response = await api.get('/api/items', {
          params: {
            page: this.currentPage,
            size: this.pageSize,
            sort: 'createdAt,desc'
          }
        });
        this.products = response.data.items.content;
        this.totalPages = response.data.items.totalPages;
        this.totalProducts = response.data.items.totalElements;
        this.sellingProductsCount = response.data.sellingCount;
        this.soldOutProductsCount = response.data.soldOutCount;
      } catch (error) {
        console.error('상품 목록을 불러오는 데 실패했습니다:', error);
        alert('상품 목록을 불러오는 데 실패했습니다.');
      }
    },
    
    // [상품 검색 API 호출]
    async searchProductsAPI(keyword) {
      try {
        const response = await api.get('/api/items/search/keyword', {
          params: { 
            keyword,
            page: this.currentPage,
            size: this.pageSize,
            sort: 'createdAt,desc'
          }
        });
        this.products = response.data.items.content;
        this.totalPages = response.data.items.totalPages;
        this.totalProducts = response.data.items.totalElements;
        this.sellingProductsCount = response.data.sellingCount;
        this.soldOutProductsCount = response.data.soldOutCount;
      } catch (error) {
        console.error('상품 검색에 실패했습니다:', error);
        alert('상품 검색에 실패했습니다.');
      }
    },
    
    // [상품 검색]
    async searchProducts() {
      this.currentPage = 0; // 검색 시 첫 페이지로 리셋
      await this.fetchProducts();
    },

    // [판매 내역 불러오기]
    async fetchSales() {
      const keyword = this.salesSearchKeyword.trim();
      
      // 검색 키워드가 있으면 검색 API, 없으면 전체 목록 API 호출
      if (keyword) {
        await this.searchSalesAPI(keyword);
      } else {
        await this.fetchSalesAPI();
      }
    },
    
    // [전체 판매 내역 API 호출]
    async fetchSalesAPI() {
      try {
        const response = await api.get('/admin/orders', {
          params: {
            page: this.salesCurrentPage,
            size: this.salesPageSize,
            sort: 'orderDate'
          }
        });
        this.salesData = response.data.orders;
        this.salesTotalPages = response.data.totalPages;
        this.salesTotalElements = response.data.totalElements;
      } catch (error) {
        console.error('판매 내역을 불러오는 데 실패했습니다:', error);
        alert('판매 내역을 불러오는 데 실패했습니다.');
      }
    },
    
    // [판매 내역 검색 API 호출]
    async searchSalesAPI(keyword) {
      try {
        const response = await api.get('/admin/orders/search', {
          params: { 
            keyword,
            page: this.salesCurrentPage,
            size: this.salesPageSize
          }
        });
        this.salesData = response.data.orders;
        this.salesTotalPages = response.data.totalPages;
        this.salesTotalElements = response.data.totalElements;
      } catch (error) {
        console.error('판매 내역 검색에 실패했습니다:', error);
        alert('판매 내역 검색에 실패했습니다.');
      }
    },
    
    // [판매 내역 검색]
    async searchSales() {
      this.salesCurrentPage = 0; // 검색 시 첫 페이지로 리셋
      await this.fetchSales();
    },
    
        // [상품 관리 페이지네이션]
        changePageSize() {
          this.currentPage = 0;
          this.fetchProducts();
        },
        goToFirstPage() {
          this.currentPage = 0;
          this.fetchProducts();
        },
        prevPage() {
          if (this.currentPage > 0) {
            this.currentPage--;
            this.fetchProducts();
          }
        },
        nextPage() {
          if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.fetchProducts();
          }
        },
        goToLastPage() {
          this.currentPage = this.totalPages - 1;
          this.fetchProducts();
        },
        goToPage() {
          const page = this.jumpToPage - 1;
          if (page >= 0 && page < this.totalPages) {
            this.currentPage = page;
            this.fetchProducts();
          } else {
            alert('유효하지 않은 페이지 번호입니다.');
          }
        },

        // [판매 내역 페이지네이션]
        changeSalesPageSize() {
          this.salesCurrentPage = 0;
          this.fetchSales();
        },
        goToSalesFirstPage() {
          this.salesCurrentPage = 0;
          this.fetchSales();
        },
        salesPrevPage() {
          if (this.salesCurrentPage > 0) {
            this.salesCurrentPage--;
            this.fetchSales();
          }
        },
        salesNextPage() {
          if (this.salesCurrentPage < this.salesTotalPages - 1) {
            this.salesCurrentPage++;
            this.fetchSales();
          }
        },
        goToSalesLastPage() {
          this.salesCurrentPage = this.salesTotalPages - 1;
          this.fetchSales();
        },
        goToSalesPage() {
          const page = this.salesJumpToPage - 1;
          if (page >= 0 && page < this.salesTotalPages) {
            this.salesCurrentPage = page;
            this.fetchSales();
          } else {
            alert('유효하지 않은 페이지 번호입니다.');
          }
        },
    
        // [카테고리 목록 불러오기]
        async fetchCategories() {
      try {
        const response = await api.get('/api/categories');
        const categoryDataFromAPI = response.data.categoryData || {};
        
        // CategoryInfo 배열을 문자열 배열로 변환
        this.categoryData = {};
        for (const [classification, categories] of Object.entries(categoryDataFromAPI)) {
          this.categoryData[classification] = categories.map(cat => cat.category);
        }
      } catch (error) {
        console.error('카테고리 목록을 불러오는 데 실패했습니다:', error);
        // 실패 시 기본값 사용
        this.categoryData = {
          '아우터': ['바람막이', '수트/블레이저', '가디건', '후드 집업', '무스탕', '패딩', '코트'],
          '상의': ['반소매 티셔츠', '긴소매 티셔츠', '맨투맨/스웨트', '후드 티셔츠', '니트/스웨터', '피케/카라', '셔츠/블라우스', '민소매'],
          '하의': ['데님 팬츠', '슬랙스', '코튼 팬츠', '조거/트레이닝', '숏 팬츠', '카고', '와이드', '부츠컷'],
        };
      }
    }
  },
  mounted() {
    this.fetchProducts();
    this.fetchCategories();
    this.fetchSales(); // 판매 내역 불러오기
  }
};
</script>

<style scoped>
.main-wrap {
  max-width: 1400px;
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
  //table-layout: fixed;
}

th {
  background: #f9fafb;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid var(--line);
  font-size: 14px;
}

/*
th:nth-child(1) { width: 15%; }
th:nth-child(2) { width: 25%; }
th:nth-child(3) { width: 15%; }
th:nth-child(4) { width: 12%; }
th:nth-child(5) { width: 9%; }
th:nth-child(6) { width: 13%; }
th:nth-child(7) { width: 8%; }
*/

td {
  padding: 12px;
  border-bottom: 1px solid var(--line);
  vertical-align: middle;
}

td:nth-child(1) {
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

/* MyPage.vue 스타일과 유사하게 재작성 */
.space-y-6 > * + * {
  margin-top: 1.5rem;
}

.sale-card {
  border: 1px solid var(--line);
  border-radius: 12px;
  overflow: hidden;
  background: #ffffff;
}

.sale-card-header {
  background-color: #f9fafb;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  border-bottom: 1px solid var(--line);
}

.sale-card-footer {
  background-color: #f9fafb;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #374151;
  border-top: 1px solid var(--line);
}

.flex { display: flex; }
.items-center { align-items: center; }
.gap-4 { gap: 1rem; }

.order-no {
  font-size: 12px;
  color: var(--muted);
  font-weight: normal;
}

.p-4 { padding: 1rem; }

.sale-item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  background-color: #f9fafb;
  flex-shrink: 0;
  border: 1px solid var(--line);
  overflow: hidden;
}

.w-full { width: 100%; }
.h-full { height: 100%; }
.object-cover { object-fit: cover; }

.grow { flex-grow: 1; }

.font-bold { font-weight: 700; }
.text-gray-800 { color: #1f2937; }
.line-clamp-1 {
  overflow: hidden;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination-controls button {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 6px 12px;
  cursor: pointer;
}

.pagination-controls button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.page-info {
  font-size: 14px;
  font-weight: 600;
  margin: 0 10px;
}

.items-per-page, .page-jump {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.items-per-page select, .page-jump input {
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  padding: 6px;
  width: 70px;
}

.page-jump button {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 6px 12px;
  cursor: pointer;
}


.text-xs { font-size: 12px; }
.text-gray-500 { color: #6b7280; }
.mt-1 { margin-top: 4px; }

.text-sm { font-size: 14px; }
.font-semibold { font-weight: 600; }
.text-gray-700 { color: #374151; }

.empty-sales {
  text-align: center;
  padding: 40px;
  color: var(--muted);
  border: 1px dashed var(--line);
  border-radius: 12px;
}
</style>
