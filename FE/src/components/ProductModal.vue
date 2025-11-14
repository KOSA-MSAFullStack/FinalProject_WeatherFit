<!-- ProductModal.vue -->
<!-- 상품 등록/수정 모달 -->

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>{{ isEditMode ? '상품 수정' : '상품 등록' }}</h2>
        <button class="modal-close" @click="$emit('close')">&times;</button>
      </div>
      <div class="modal-body">
        <div class="grid2">
          <div class="form-group">
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" v-model="product.itemName">
          </div>
          <div class="form-group">
            <label for="itemCode">상품 코드</label>
            <input type="text" id="itemCode" v-model="product.itemCode">
          </div>
        </div>

        <div class="grid2">
          <div class="form-group">
            <label for="price">가격</label>
            <input type="number" id="price" placeholder="숫자만 입력" v-model.number="product.price">
          </div>
          <div class="form-group">
            <label for="stockQuantity">재고 수량</label>
            <input type="number" id="stockQuantity" placeholder="숫자만 입력" v-model.number="product.quantity">
          </div>
        </div>

        <div class="grid2">
          <div class="form-group">
            <label for="classification">분류</label>
            <select id="classification" v-model="product.classification" @change="updateCategoryOptions">
              <option v-for="(categories, classification) in categoryData" :key="classification" :value="classification">
                {{ classification }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="category">카테고리</label>
            <select id="category" v-model="product.category">
              <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
            </select>
          </div>
        </div>

        <div class="grid2">
          <div class="form-group">
            <label>성별</label>
            <div class="radio-group">
              <input type="radio" id="male" value="남성" v-model="product.selectedGender"><label for="male">남성</label>
              <input type="radio" id="female" value="여성" v-model="product.selectedGender"><label for="female">여성</label>
              <input type="radio" id="unisex" value="남여공용" v-model="product.selectedGender"><label for="unisex">남여공용</label>
            </div>
          </div>
          <div class="form-group">
            <label>계절</label>
            <div class="radio-group">
              <input type="checkbox" id="spring" value="봄" v-model="product.selectedSeasons"><label for="spring">봄</label>
              <input type="checkbox" id="summer" value="여름" v-model="product.selectedSeasons"><label for="summer">여름</label>
              <input type="checkbox" id="fall" value="가을" v-model="product.selectedSeasons"><label for="fall">가을</label>
              <input type="checkbox" id="winter" value="겨울" v-model="product.selectedSeasons"><label for="winter">겨울</label>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="itemImage">상품 이미지</label>
          <div class="custom-file-input-box">
            <input type="file" id="itemImage" accept="image/*" @change="handleImageUpload" class="hidden-file-input">
            <label for="itemImage" class="file-select-button">업로드</label>
            <span class="selected-file-name">{{ selectedFileName }}</span>
          </div>
          <div id="imgPreview" class="img-preview" :style="{ backgroundImage: `url(${imagePreview})` }"></div>
        </div>

        <hr style="border:none; border-top:1px solid var(--line); margin:8px 0 20px;">

        <div class="form-group">
          <button class="btn ghost" id="btnGenerateAI" type="button" @click="generateAIDescription" v-if="!aiBoxVisible">AI 설명 생성</button>
          <div class="ai-box" id="aiBox" v-if="aiBoxVisible">
            <div class="ai-box-header">
              <h3>AI 설명</h3>
              <button class="btn ghost" type="button" id="btnRegenAI" style="font-size:13px; padding:6px 10px;" @click="regenerateAIDescription">다시 생성</button>
            </div>
            <textarea id="aiDescription" placeholder="AI가 생성한 설명이 여기에 표시됩니다." v-model="product.aiDescription"></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <div>
          <button v-if="isEditMode" class="btn danger" @click="handleDelete">삭제하기</button>
        </div>
        <div>
          <button class="btn ghost" @click="$emit('close')">취소</button>
          <button class="btn" @click="handleSubmit">{{ isEditMode ? '저장하기' : '등록하기' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/utils/axios';

export default {
  name: 'ProductModal',
  props: {
    productToEdit: {
      type: Object,
      default: null,
    },
  },
  emits: ['close', 'submit', 'delete'],
  data() {
    return {
      product: {
        itemName: '',
        itemCode: '', // 상품 코드 추가
        price: null,
        quantity: null,
        classification: '상의',
        category: '',
        selectedGender: '', // 선택된 성별 (단일 문자열)
        selectedSeasons: [], // 선택된 계절 (배열)
        image: null,
        aiDescription: '',
        maxTemperature: null, // 최고 기온 추가
        minTemperature: null,  // 최저 기온 추가
      },
      selectedFileName: '선택된 파일 없음', // 선택된 파일명 표시
      categoryData: {
        '아우터': ['바람막이', '수트/블레이저', '가디건', '후드 집업', '무스탕', '패딩', '코트'],
        '상의': ['반소매 티셔츠', '긴소매 티셔츠', '맨투맨/스웨트', '후드 티셔츠', '니트/스웨터', '피케/카라', '셔츠/블라우스', '민소매'],
        '하의': ['데님 팬츠', '슬랙스', '코튼 팬츠', '조거/트레이닝', '숏 팬츠', '카고', '와이드', '부츠컷'],
      },
      genderMap: { // 백엔드 코드와 프론트엔드 표시명 매핑
        'M': '남성',
        'F': '여성',
        'C': '남여공용',
        '남성': 'M',
        '여성': 'F',
        '남여공용': 'C',
      },
      categories: [],
      imagePreview: '',
      aiBoxVisible: false,
    };
  },
  computed: {
    isEditMode() {
      return !!this.productToEdit;
    },
  },
  watch: {
    productToEdit: {
      handler(newVal) {
        if (newVal) {
          // 깊은 복사를 통해 부모 컴포넌트의 데이터가 직접 수정되는 것을 방지
          this.product = JSON.parse(JSON.stringify(newVal));
          this.imagePreview = newVal.imageURL || '';
          this.product.aiDescription = !!newVal.aiDescription ? newVal.aiDescription : ''; // aiDescription이 null일 경우 빈 문자열로 초기화
          this.product.maxTemperature = newVal.maxTemperature || null; // 최고 기온 로드
          this.product.minTemperature = newVal.minTemperature || null;  // 최저 기온 로드

          // 파일 이름 초기화
          this.selectedFileName = newVal.imageURL ? newVal.imageURL.substring(newVal.imageURL.lastIndexOf('/') + 1) : '선택된 파일 없음';

        } else {
          this.product = {
            itemName: '',
            price: null,
            quantity: null,
            classification: '상의',
            category: '',
            selectedGender: '',
            selectedSeasons: [],
            image: null,
            aiDescription: '',
            maxTemperature: null, // 최고 기온 초기화
            minTemperature: null,  // 최저 기온 초기화
          };
          this.imagePreview = '';
          this.aiBoxVisible = false;
          this.selectedFileName = '선택된 파일 없음'; // 등록 모드일 때 파일 이름 초기화
        }
        this.updateCategoryOptions();
      },
      immediate: true,
    },
  },

  methods: {
    updateCategoryOptions() {
      this.categories = this.categoryData[this.product.classification] || [];
      if (this.categories.length > 0 && !this.categories.includes(this.product.category)) {
        this.product.category = this.categories[0];
      }
    },

    // 이미지 업로드시 미리보기
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (!file) {
        return;
      }

      this.product.image = file;
      this.selectedFileName = file.name;

      const reader = new FileReader();
      reader.onload = (e) => {
        this.imagePreview = e.target.result;
      };
      reader.readAsDataURL(file);
    },

    // AI 설명 생성
    async generateAIDescription() {
      if (!this.product.image) {
        alert('AI 설명을 생성하려면 상품 이미지를 먼저 등록해주세요.');
        return;
      }

      this.aiBoxVisible = true;
      this.product.aiDescription = 'AI 설명 생성 중... 잠시만 기다려 주세요.'; // 로딩 메시지

      const formData = new FormData();
      formData.append('itemName', this.product.itemName);
      formData.append('category', this.product.category);
      formData.append('gender', this.genderMap[this.product.selectedGender] || '');
      this.product.selectedSeasons.forEach(season => {
        formData.append('seasons', season);
      });
      formData.append('image', this.product.image);

      try {
        const response = await api.post('/admin/items/generate-description', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        this.product.aiDescription = response.data.content;
        this.product.maxTemperature = response.data.maxTemperature; // 최고 기온 할당
        this.product.minTemperature = response.data.minTemperature;  // 최저 기온 할당
      } catch (error) {
        console.error('AI 설명 생성 실패:', error);
        this.product.aiDescription = 'AI 설명 생성에 실패했습니다. 다시 시도해주세요.';
        alert('AI 설명 생성 중 오류가 발생했습니다: ' + (error.response?.data?.message || error.message));
      }
    },
    regenerateAIDescription() {
      // 다시 생성 버튼 클릭 시 generateAIDescription 로직 재사용
      this.generateAIDescription();
    },

    handleSubmit() {
      // 제출 전에 성별 데이터를 백엔드 형식으로 변환 (단일 문자열 코드)
      const submittedProduct = { ...this.product };
      if (submittedProduct.selectedGender) {
        submittedProduct.gender = this.genderMap[submittedProduct.selectedGender];
      } else {
        submittedProduct.gender = null; // 선택된 성별이 없으면 null
      }
      delete submittedProduct.selectedGender; // 임시 필드 삭제

      // 계절 데이터는 이미 배열이므로 그대로 사용
      submittedProduct.seasonName = submittedProduct.selectedSeasons;
      delete submittedProduct.selectedSeasons; // 임시 필드 삭제

      // 최고/최저 기온 추가
      submittedProduct.maxTemperature = this.product.maxTemperature;
      submittedProduct.minTemperature = this.product.minTemperature;

      this.$emit('submit', submittedProduct);
    },

    handleDelete() {
      if (confirm('정말로 이 상품을 삭제하시겠습니까?')) {
        this.$emit('delete', this.product.itemId);
      }
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 99;
  background: rgba(0, 0, 0, .5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.modal-content {
  background: var(--panel);
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, .15);
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px;
  border-bottom: 1px solid var(--line);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
}

.modal-close {
  font-size: 24px;
  cursor: pointer;
  color: var(--muted);
  border: none;
  background: none;
}

.modal-body {
  padding: 16px;
  overflow-y: auto;
}

.modal-footer {
  padding: 16px;
  border-top: 1px solid var(--line);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 6px;
  font-size: 14px;
  text-align: left;
}

input,
select,
textarea {
  width: 100%;
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
  font-size: 15px;
  text-align: left;
}

textarea {
  resize: vertical;
  min-height: 200px;
}

input[type="file"] {
  padding: 8px;
}

.hidden-file-input {
  display: none;
}

.custom-file-input-box {
  display: flex;
  align-items: center;
  border: 1px solid var(--line);
  border-radius: 10px;
  background: #f9fafb;
  padding: 4px; /* 내부 여백 */
  gap: 8px;
  margin-top: 8px;
}

.file-select-button {
  cursor: pointer;
  padding: 10px 16px; /* .btn과 동일한 패딩 */
  border-radius: 0px; /* 모서리를 뾰족하게 */
  font-weight: 700; /* .btn과 동일한 font-weight */
  transition: opacity .2s;
  background: #ffffff; /* .btn.ghost와 동일한 배경색 */
  border: 1px solid var(--line); /* .btn.ghost와 동일한 테두리 */
  color: #111827; /* .btn.ghost와 동일한 글자색 */
  white-space: nowrap;
  flex-shrink: 0;
}

.file-select-button:hover {
  opacity: .9;
}

.selected-file-name {
  flex-grow: 1;
  color: var(--muted);
  font-size: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 4px; /* 텍스트 내부 여백 */
}

.grid2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.btn {
  background: linear-gradient(135deg, var(--grad1), var(--grad2));
  color: #ffffff;
  border: none;
  border-radius: 10px;
  padding: 10px 16px;
  font-weight: 700;
  cursor: pointer;
  transition: opacity .2s;
}

.btn:hover {
  opacity: .9
}

.btn.ghost {
  background: #ffffff;
  border: 1px solid var(--line);
  color: #111827;
}

.btn.danger {
  background: var(--danger);
  color: #ffffff;
}

.radio-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.radio-group label {
  display: inline-block;
  padding: 8px 14px;
  border: 1px solid var(--line);
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
  transition: background .2s, border-color .2s;
}

.radio-group input {
  display: none;
}

.radio-group input:checked+label {
  background: #eef2ff;
  border-color: #818cf8;
  font-weight: 600;
  color: #4338ca;
}

.ai-box {
  background: #f9fafc;
  border: 1px solid var(--line);
  border-radius: 12px;
  padding: 12px;
  margin-top: 6px;
}

.ai-box-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.ai-box-header h3 {
  margin: 0;
  font-size: 15px;
}

.ai-box textarea {
  width: 100%;
  border: 1px solid var(--line);
  border-radius: 8px;
  padding: 8px 10px;
  font-size: 14px;
  line-height: 1.5;
}

.img-preview {
  width: 100%;
  aspect-ratio: 4/3;
  border: 1px solid var(--line);
  border-radius: 12px;
  margin-top: 8px;
  background: #fafafa center/contain no-repeat;
}
</style>
