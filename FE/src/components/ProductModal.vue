<!-- ProductModal.vue -->
<!-- 상품 등록/수정 모달 -->

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>상품 등록</h2>
        <button class="modal-close" @click="$emit('close')">&times;</button>
      </div>
      <div class="modal-body">
        <div class="form-group">
          <label for="itemName">상품명</label>
          <input type="text" id="itemName" v-model="product.itemName">
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
              <input type="checkbox" id="male" value="남성" v-model="product.gender"><label for="male">남성</label>
              <input type="checkbox" id="female" value="여성" v-model="product.gender"><label for="female">여성</label>
              <input type="checkbox" id="unisex" value="남여공용" v-model="product.gender"><label for="unisex">남여공용</label>
            </div>
          </div>
          <div class="form-group">
            <label>계절</label>
            <div class="radio-group">
              <input type="checkbox" id="spring" value="봄" v-model="product.season"><label for="spring">봄</label>
              <input type="checkbox" id="summer" value="여름" v-model="product.season"><label for="summer">여름</label>
              <input type="checkbox" id="fall" value="가을" v-model="product.season"><label for="fall">가을</label>
              <input type="checkbox" id="winter" value="겨울" v-model="product.season"><label for="winter">겨울</label>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="itemImage">상품 이미지</label>
          <input type="file" id="itemImage" accept="image/*" @change="handleImageUpload">
          <div id="imgPreview" class="img-preview" :style="{ backgroundImage: `url(${imagePreview})` }"></div>
        </div>

        <hr style="border:none; border-top:1px solid var(--line); margin:8px 0 20px;">

        <div class="form-group">
          <button class="btn ghost" id="btnGenerateAI" type="button" @click="generateAIDescription" v-if="!aiBoxVisible">AI 설명 생성</button>
          <div class="ai-box" id="aiBox" v-if="aiBoxVisible">
            <div class="ai-box-header">
              <h3>생성된 설명</h3>
              <button class="btn ghost" type="button" id="btnRegenAI" style="font-size:13px; padding:6px 10px;" @click="regenerateAIDescription">다시 생성</button>
            </div>
            <textarea id="aiDescription" placeholder="AI가 생성한 설명이 여기에 표시됩니다." v-model="product.aiDescription"></textarea>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <div></div>
        <div>
          <button class="btn ghost" @click="$emit('close')">취소</button>
          <button class="btn" @click="handleSubmit">등록하기</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductModal',
  emits: ['close', 'submit'],
  data() {
    return {
      product: {
        itemName: '',
        price: null,
        quantity: null,
        classification: '상의',
        category: '',
        gender: [],
        season: [],
        image: null,
        aiDescription: '',
      },
      categoryData: {
        '아우터': ['바람막이', '수트/블레이저', '가디건', '후드 집업', '무스탕', '패딩', '코트'],
        '상의': ['반소매 티셔츠', '긴소매 티셔츠', '맨투맨/스웨트', '후드 티셔츠', '니트/스웨터', '피케/카라', '셔츠/블라우스', '민소매'],
        '하의': ['데님 팬츠', '슬랙스', '코튼 팬츠', '조거/트레이닝', '숏 팬츠', '카고', '와이드', '부츠컷'],
      },
      categories: [],
      imagePreview: '',
      aiBoxVisible: false,
    };
  },
  created() {
    this.updateCategoryOptions();
  },
  methods: {
    updateCategoryOptions() {
      this.categories = this.categoryData[this.product.classification] || [];
      if (this.categories.length > 0) {
        this.product.category = this.categories[0];
      }
    },
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.product.image = file;
        const reader = new FileReader();
        reader.onload = (e) => {
          this.imagePreview = e.target.result;
        };
        reader.readAsDataURL(file);
      }
    },
    generateAIDescription() {
      this.aiBoxVisible = true;
      this.product.aiDescription = '이 상품은 가볍고 따뜻한 소재로 제작되어 일상 및 출퇴근용으로 적합합니다. 10~18°C 구간에서 활용도가 높으며, 간절기 착용 시 쾌적함을 유지합니다.';
    },
    regenerateAIDescription() {
      this.product.aiDescription = '재생성된 예시: 통기성이 좋아 실내 활동에도 쾌적하며, 약한 바람이나 간헐적 비 날씨에도 무난하게 착용 가능합니다.';
    },
    handleSubmit() {
      // 부모 컴포넌트로 데이터 전송
      this.$emit('submit', this.product);
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
  justify-content: flex-end;
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
  min-height: 100px;
}

input[type="file"] {
  padding: 8px;
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
