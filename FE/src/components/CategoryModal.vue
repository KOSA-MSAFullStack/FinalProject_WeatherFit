<!-- CategoryModal.vue -->
<!-- 카테고리 관리 모달 -->

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h2>카테고리 관리</h2>
        <button class="modal-close" @click="$emit('close')">&times;</button>
      </div>
      
      <div class="modal-body">
        <div class="grid-manage">
          <!-- 왼쪽: 분류 선택 -->
          <div class="form-group">
            <label for="classification">분류</label>
            <select id="classification" v-model="selectedClassification" @change="loadCategories">
              <option v-for="(categories, classification) in localCategoryData" :key="classification" :value="classification">
                {{ classification }}
              </option>
            </select>
          </div>

          <!-- 오른쪽: 카테고리 관리 -->
          <div>
            <label>카테고리</label>
            <div class="add-category-form">
              <input 
                type="text" 
                v-model="newCategoryName" 
                placeholder="새 카테고리명 입력" 
                @keyup.enter="addCategory"
              >
              <button class="btn" @click="addCategory">추가</button>
            </div>
            <div class="category-list-container">
              <div v-if="categories.length === 0" class="empty-message">
                등록된 카테고리가 없습니다.
              </div>
              <div 
                v-for="(category, index) in categories" 
                :key="`${selectedClassification}-${index}`" 
                class="category-item"
              >
                <span>{{ category }}</span>
                <div class="category-item-actions">
                  <button class="btn small ghost" @click="editCategory(category)">수정</button>
                  <button class="btn small danger" @click="deleteCategory(category)">삭제</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn ghost" @click="$emit('close')">닫기</button>
        <button class="btn" @click="saveChanges">저장하기</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CategoryModal',
  props: {
    categoryData: {
      type: Object,
      required: true,
    },
  },
  emits: ['close', 'save'],
  data() {
    return {
      selectedClassification: '아우터',
      newCategoryName: '',
      localCategoryData: {},
      categories: [],
    };
  },
  mounted() {
    // props의 categoryData를 localCategoryData에 복사
    this.localCategoryData = JSON.parse(JSON.stringify(this.categoryData));
    // 첫 분류 선택
    const classifications = Object.keys(this.localCategoryData);
    if (classifications.length > 0) {
      this.selectedClassification = classifications[0];
    }
    this.loadCategories();
  },
  methods: {
    // 선택된 분류의 카테고리 목록 로드
    loadCategories() {
      this.categories = [...(this.localCategoryData[this.selectedClassification] || [])];
    },

    // 카테고리 추가
    addCategory() {
      const trimmedName = this.newCategoryName.trim();
      
      if (!trimmedName) {
        alert('카테고리명을 입력해주세요.');
        return;
      }

      if (this.categories.includes(trimmedName)) {
        alert('이미 존재하는 카테고리입니다.');
        return;
      }

      this.categories.push(trimmedName);
      this.localCategoryData[this.selectedClassification] = [...this.categories];
      this.newCategoryName = '';
    },

    // 카테고리 수정
    editCategory(oldName) {
      const newName = prompt(`'${oldName}'의 새 이름을 입력하세요.`, oldName);

      if (!newName || newName.trim() === '') {
        return;
      }

      const trimmedNewName = newName.trim();
      if (trimmedNewName === oldName) {
        return;
      }

      if (this.categories.includes(trimmedNewName)) {
        alert('이미 존재하는 카테고리명입니다.');
        return;
      }

      const index = this.categories.indexOf(oldName);
      if (index > -1) {
        this.categories[index] = trimmedNewName;
        this.localCategoryData[this.selectedClassification] = [...this.categories];
      }
    },

    // 카테고리 삭제
    deleteCategory(categoryName) {
      if (!confirm(`'${categoryName}' 카테고리를 정말 삭제하시겠습니까?`)) {
        return;
      }

      this.categories = this.categories.filter(cat => cat !== categoryName);
      this.localCategoryData[this.selectedClassification] = [...this.categories];
    },

    // 변경사항 저장
    saveChanges() {
      this.$emit('save', this.localCategoryData);
      alert('변경사항이 저장되었습니다.');
      this.$emit('close');
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
  max-width: 600px;
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
}

input,
select {
  width: 100%;
  background: #ffffff;
  color: var(--text);
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
  font-size: 15px;
}

.grid-manage {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 16px;
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
  white-space: nowrap;
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

.btn.small {
  padding: 6px 10px;
  font-size: 13px;
  font-weight: normal;
}

.category-list-container {
  border: 1px solid var(--line);
  border-radius: 10px;
  height: 250px;
  overflow-y: auto;
  padding: 8px;
  background: #ffffff;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.category-item:hover {
  background-color: #f9fafb;
}

.category-item-actions {
  display: flex;
  gap: 6px;
}

.add-category-form {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.add-category-form input {
  flex: 1;
}

.empty-message {
  text-align: center;
  color: var(--muted);
  padding-top: 80px;
  font-size: 14px;
}
</style>
