<template>
  <!-- max-w-xl 유지 -->
  <main class="max-w-xl mx-auto px-4 py-8 md:py-10">
    <h1 class="text-3xl font-extrabold text-gray-900 mb-6 text-center md:text-4xl">
      회원가입
    </h1>
    <!-- 폼 컨테이너: 패널 스타일 적용 -->
    <div class="bg-white border border-gray-200 shadow-xl rounded-2xl p-6 md:p-10">
      <form @submit.prevent="handleSignup" class="space-y-6">
        
        <!-- 이름 필드 -->
        <div class="space-y-1">
          <label for="name" class="block font-semibold text-sm text-gray-700">이름</label>
          <input 
            v-model="signupData.name"
            type="text" 
            id="name"
            required 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
          />
        </div>
        
        <!-- 아이디(이메일) 필드 -->
        <div class="space-y-1">
          <label for="email" class="block font-semibold text-sm text-gray-700">아이디 (이메일)</label>
          <div class="flex gap-2">
            <input 
              v-model="signupData.email"
              type="email" 
              id="email"
              required 
              :disabled="isEmailChecked && isEmailAvailable"
              @input="isEmailChecked = false; isEmailAvailable = false"
              class="grow bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition disabled:bg-gray-50 disabled:cursor-not-allowed"
            />
            <button 
              type="button" 
              @click="checkEmailDuplication"
              :disabled="!signupData.email || isEmailChecked && isEmailAvailable"
              class="shrink-0 px-4 py-3 rounded-xl font-medium text-sm transition-all"
              :class="{
                'bg-blue-500 text-white hover:bg-blue-600 shadow-md': !isEmailChecked,
                'bg-gray-300 text-gray-700 cursor-not-allowed': isEmailChecked && isEmailAvailable,
              }"
            >
              {{ isEmailChecked && isEmailAvailable ? '확인 완료' : '중복확인' }}
            </button>
          </div>
          <!-- 중복 확인 메시지 -->
          <p v-if="isEmailChecked && message" class="mt-1 text-xs font-medium pl-1">
            <span v-if="isEmailAvailable === true" class="text-green-600 flex items-center">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                {{ message }}
            </span>
            <span v-else-if="isEmailAvailable === false" class="text-red-600 flex items-center">
                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                {{ message }}
            </span>
          </p>
        </div>
        
        <!-- 비밀번호 필드 -->
        <div class="space-y-1">
          <label for="password" class="block font-semibold text-sm text-gray-700">비밀번호</label>
          <input 
            v-model="signupData.password"
            type="password" 
            id="password"
            required 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            :class="{
              'border-red-500 focus:ring-red-500': !isPasswordValid && passwordValidationMessage,
              'border-green-500 focus:ring-green-500': isPasswordValid
            }"
          />
          <p v-if="passwordValidationMessage"
            class="mt-1 text-xs font-medium pl-1"
            :class="{
              'text-green-600': isPasswordValid,
              'text-red-600': !isPasswordValid
            }"
          >
            {{ passwordValidationMessage }}
          </p>
        </div>
        
        <!-- 비밀번호 확인 필드 -->
        <div class="space-y-1">
          <label for="passwordConfirm" class="block font-semibold text-sm text-gray-700">비밀번호 확인</label>
          <input 
            v-model="signupData.passwordConfirm"
            type="password" 
            id="passwordConfirm"
            required 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
            :class="{
              'border-red-500 focus:ring-red-500': passwordMismatch,
              'border-green-500 focus:ring-green-500': !passwordMismatch && passwordConfirmMessage
            }"
          />
          <p v-if="passwordConfirmMessage" 
            class="mt-1 text-xs font-medium pl-1"
            :class="{
              'text-green-600': !passwordMismatch,
              'text-red-600': passwordMismatch
            }"
          >
            {{ passwordConfirmMessage }}
          </p>
        </div>
        
        <!-- 생년월일 필드 -->
        <div class="space-y-1">
          <label for="birth" class="block font-semibold text-sm text-gray-700">생년월일</label>
          <input 
            v-model="signupData.birth"
            type="date" 
            id="birth"
            required 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
          />
        </div>
        
        <!-- 성별 필드 -->
        <div class="space-y-1">
          <label class="block font-semibold text-sm text-gray-700">성별</label>
          <div class="flex gap-2">
            <input type="radio" id="male" value="male" v-model="signupData.gender" name="gender" class="hidden" />
            <label 
              for="male" 
              :class="[
                'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                { 'bg-indigo-100 border-indigo-400 text-indigo-700 font-semibold': signupData.gender === 'male' }
              ]"
            >
              남성
            </label>
            
            <input type="radio" id="female" value="female" v-model="signupData.gender" name="gender" class="hidden" />
            <label 
              for="female" 
              :class="[
                'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                { 'bg-indigo-100 border-indigo-400 text-indigo-700 font-semibold': signupData.gender === 'female' }
              ]"
            >
              여성
            </label>
          </div>
        </div>

        <!-- 연락처 입력 -->
        <div class="space-y-1">
          <label for="phone" class="block font-semibold text-sm text-gray-700">연락처</label>
          <input 
            v-model="signupData.phone"
            @input="formatPhoneNumber"
            type="tel" 
            id="phone" 
            placeholder="010-0000-0000" 
            required 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition"
          />
        </div>

        <!-- 주소 입력 -->
        <div class="space-y-1">
          <label class="block font-semibold text-sm text-gray-700">주소 (배송주소)</label>
          <div class="grid grid-cols-3 gap-2">
            <input 
              v-model="signupData.postcode"
              type="text" 
              id="postcode" 
              placeholder="우편번호" 
              readonly 
              class="col-span-2 bg-gray-100 border border-gray-300 rounded-xl p-3 text-base outline-none" 
            />
            <button 
              type="button" 
              @click="findAddress" 
              class="col-span-1 bg-white border border-gray-300 text-gray-700 rounded-xl font-medium text-sm hover:bg-gray-100 transition duration-150"
            >
              주소 찾기
            </button>
          </div>
          <input 
            v-model="signupData.address"
            type="text" 
            id="address" 
            placeholder="기본 주소" 
            readonly 
            class="w-full bg-gray-100 border border-gray-300 rounded-xl p-3 text-base mt-2 outline-none" 
          />
          <input 
            v-model="signupData.detailAddress"
            type="text" 
            id="detailAddress" 
            placeholder="상세 주소" 
            class="w-full bg-white border border-gray-300 rounded-xl p-3 text-base mt-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition" 
          />
        </div>

        <!-- 날씨 민감도 -->
        <div class="space-y-1">
          <label class="block font-semibold text-sm text-gray-700">날씨 민감도</label>
          <div class="flex gap-2 flex-wrap">
            <input type="radio" id="sens_cold" value="cold" v-model="signupData.temperatureSensitivity" name="temperatureSensitivity" class="hidden">
            <label 
              for="sens_cold" 
              :class="[
                'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                { 'bg-indigo-100 border-indigo-400 text-indigo-700 font-semibold': signupData.temperatureSensitivity === 'cold' }
              ]"
            >
              추위 민감
            </label>
            
            <input type="radio" id="sens_normal" value="normal" v-model="signupData.temperatureSensitivity" name="temperatureSensitivity" class="hidden">
            <label 
              for="sens_normal" 
              :class="[
                'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                { 'bg-indigo-100 border-indigo-400 text-indigo-700 font-semibold': signupData.temperatureSensitivity === 'normal' }
              ]"
            >
              보통
            </label>
            
            <input type="radio" id="sens_hot" value="hot" v-model="signupData.temperatureSensitivity" name="temperatureSensitivity" class="hidden">
            <label 
              for="sens_hot" 
              :class="[
                'flex-1 text-center p-3 border border-gray-300 rounded-xl text-sm cursor-pointer transition-all',
                { 'bg-indigo-100 border-indigo-400 text-indigo-700 font-semibold': signupData.temperatureSensitivity === 'hot' }
              ]"
            >
              더위 민감
            </label>
          </div>
        </div>

        <!-- 가입 버튼: 그라데이션 및 강조 스타일 적용 -->
        <button 
          type="submit" 
          :disabled="!isEmailAvailable"
          class="w-full text-white py-3.5 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-lg shadow-blue-500/30 mt-6 disabled:opacity-50 disabled:shadow-none"
          style="background: linear-gradient(135deg, var(--grad1), var(--grad2));"
        >
          가입하기
        </button>
        <p v-if="!isEmailAvailable && isEmailChecked" class="text-red-500 text-center text-sm">
            가입을 진행하려면 이메일 중복 확인이 필수입니다.
        </p>
      </form>
      <div class="text-center text-sm text-gray-600 mt-6 pt-4 border-t border-gray-100">
        이미 계정이 있으신가요? 
        <RouterLink to="/login" class="text-blue-600 hover:text-blue-700 font-medium">
          로그인
        </RouterLink>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter, RouterLink } from 'vue-router';
import api from '@/utils/axios';

const router = useRouter();

// 회원가입 폼 데이터 상태
const signupData = ref({
  name: '',
  email: '',
  password: '',
  passwordConfirm: '',
  birth: '',
  gender: null,
  phone: '',
  postcode: '',
  address: '',
  detailAddress: '',
  temperatureSensitivity: null,
});

// 이메일 중복 확인 상태
const isEmailChecked = ref(false);
const isEmailAvailable = ref(null);
const message = ref('');

// 이메일 중복 확인 처리 함수
const checkEmailDuplication = async () => {
    const emailValue = signupData.value.email;
    
    if (!emailValue || !emailValue.includes('@')) {
        isEmailChecked.value = true;
        isEmailAvailable.value = false;
        message.value = '유효한 이메일 형식이 아닙니다.';
        return;
    }

    // 상태 초기화
    isEmailChecked.value = false;
    isEmailAvailable.value = null;
    message.value = '';

    try {
        const response = await api.get('/users/checkEmail', {
            params: {
                email: emailValue
            }
        });

        const responseData = response.data;
        
        // 상태 업데이트
        isEmailChecked.value = true;
        isEmailAvailable.value = responseData.available;
        message.value = responseData.message;
        
        // 메시지 로깅 (백엔드에서 받은 메시지를 사용)
        console.log(`이메일 확인 결과: ${responseData.message}`);
        
    } catch (error) {
        console.error('API 호출 실패:', error);
        
        isEmailChecked.value = true; // 버튼은 눌렀으므로 checked는 true
        isEmailAvailable.value = false; // API 실패는 가입 불가로 처리
        
        // API 실패 시 사용자에게 보여줄 메시지 설정
        if (error.response) {
          // 서버에서 에러 응답 본문을 보내줬다면 해당 메시지를 사용
          message.value = error.response.data.message || '서버 응답 오류가 발생했습니다.';
        } else {
          message.value = '네트워크 연결 상태를 확인해주세요.';
        }
    }
};

const passwordMismatch = ref(false); // 비밀번호 불일치 여부
const passwordConfirmMessage = ref(''); // 보여줄 메시지
// 비밀번호 유효성 검사 상태
const isPasswordValid = ref(false);
const passwordValidationMessage = ref('');

watch(() => signupData.value.password, (newPassword) => {
  // 정규식은 한 번만 선언해두는 것이 효율적입니다.
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;

  // 비밀번호 필드가 비어있으면 메시지를 초기화합니다.
  if (!newPassword) {
    isPasswordValid.value = false;
    passwordValidationMessage.value = '';
    return;
  }
  
  // 정규식으로 유효성 검사
  if (passwordRegex.test(newPassword)) {
    isPasswordValid.value = true;
    passwordValidationMessage.value = '사용 가능한 비밀번호입니다.';
  } else {
    isPasswordValid.value = false;
    passwordValidationMessage.value = '8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.';
  }
});

watch(
  // 감시할 대상을 배열로 지정
  [() => signupData.value.password, () => signupData.value.passwordConfirm], 
  // 값이 변경될 때 실행될 콜백 함수
  ([newPassword, newConfirm]) => {
    // 비밀번호 확인 필드가 비어있으면 메시지를 초기화
    if (!newConfirm) {
      passwordMismatch.value = false;
      passwordConfirmMessage.value = '';
      return;
    }
    
    // 두 필드의 값이 일치하는지 확인
    if (newPassword === newConfirm) {
      passwordMismatch.value = false;
      passwordConfirmMessage.value = '비밀번호가 일치합니다.';
    } else {
      passwordMismatch.value = true;
      passwordConfirmMessage.value = '비밀번호가 일치하지 않습니다.';
    }
  }
);

// 전화번호 형식 자동 변환 및 데이터 정리 함수
const formatPhoneNumber = () => {
    let raw = signupData.value.phone.replace(/[^0-9]/g, ''); // 숫자 외 모두 제거 (실제 전송할 데이터)
    let formatted = '';

    // 백엔드에 전송할 데이터는 하이픈 없는 상태로 먼저 저장
    // 💡 참고: 실제 백엔드 전송 시에는 아래 '전송 전 클렌징' 로직을 사용합니다.
    // signupData.value.phone = raw; 

    // 화면에 보여줄 형식 (하이픈 추가)
    if (raw.length > 3 && raw.length <= 7) {
        formatted = raw.slice(0, 3) + '-' + raw.slice(3);
    } else if (raw.length > 7) {
        formatted = raw.slice(0, 3) + '-' + raw.slice(3, 7) + '-' + raw.slice(7, 11);
    } else {
        formatted = raw;
    }

    // v-model에 바인딩된 값은 하이픈이 포함된 형식으로 업데이트하여 사용자에게 보여줌
    signupData.value.phone = formatted;
};

// 폼 제출 핸들러
const handleSignup = async () => {
  // 이메일 중복 확인 필수 체크
  if (!isEmailChecked.value || !isEmailAvailable.value) {
    alert('이메일 중복 확인을 완료하고, 사용 가능한 이메일인지 확인해 주세요.');
    return;
  }
    
  // 비밀번호 일치 확인
  if (signupData.value.password !== signupData.value.passwordConfirm) {
    console.error('비밀번호가 일치하지 않습니다.');
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }

  if (!isPasswordValid.value) { // 새로 만든 상태를 활용할 수도 있습니다.
    alert('비밀번호 형식이 올바르지 않습니다.');
    return;
  }

  // 필수 선택 항목 확인
  if (!signupData.value.gender) {
    alert('성별을 선택해주세요.');
    return;
  }
  if (!signupData.value.temperatureSensitivity) {
    alert('날씨 민감도를 선택해주세요.');
    return;
  }

  // 전송 전 클렌징
  // 전화번호에서 하이픈 제거
  const cleanPhone = signupData.value.phone.replace(/[^0-9]/g, '');
  // 성별 코드 변환
  const genderMap = { 'male': 'M', 'female': 'F' };
  const cleanGender = genderMap[signupData.value.gender];
  // 날씨 민감도 코드 변환
  const cleanSensitivity = signupData.value.temperatureSensitivity.toUpperCase();

  const signupDTO = {
        name: signupData.value.name,
        email: signupData.value.email,
        password: signupData.value.password,
        birth: signupData.value.birth,
        phone: cleanPhone,
        gender: cleanGender,
        temperatureSensitivity: cleanSensitivity,
        address: { 
            zipCode: signupData.value.postcode, 
            base: signupData.value.address, 
            detail: signupData.value.detailAddress
        }
    };
  
  // 회원가입 정보 로깅 (테스트)
  console.log('최종 전송 DTO:', signupDTO);

  try {
        // API 엔드포인트와 DTO를 사용하여 POST 요청
        const response = await api.post('/users/signup', signupDTO); 
        
        console.log('회원가입 성공 응답:', response.data);
        
        alert('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');
        
        // 회원가입 성공 후 로그인 페이지로 이동
        router.push('/login'); 

    } catch (error) {
        console.error('회원가입 실패:', error);
        
        let errorMessage = '회원가입에 실패했습니다. 잠시 후 다시 시도해 주세요.';
        
        if (error.response && error.response.data && error.response.data.message) {
            // 백엔드에서 에러 메시지(예: 이메일 중복 등)를 보냈을 경우
            errorMessage = error.response.data.message;
        } else if (error.message.includes('40')) {
            // 4xx 클라이언트 오류 (상세 메시지가 없을 경우)
            errorMessage = '요청 데이터에 문제가 있습니다. 입력 정보를 다시 확인해주세요.';
        }
        
        alert(errorMessage);

    }

  // 회원가입 성공 후 로그인 페이지로 이동
  router.push('/login');
};

// Daum 우편번호 찾기 함수
const findAddress = () => {
  if (typeof daum === 'undefined' || !daum.Postcode) {
    console.error('Daum Postcode Script가 로드되지 않았습니다.');
    alert('주소 찾기 기능을 사용할 수 없습니다. index.html에 Daum 스크립트 CDN을 추가했는지 확인해주세요.');
    return;
  }

  new daum.Postcode({
    oncomplete: function (data) {
      signupData.value.postcode = data.zonecode;
      signupData.value.address = data.roadAddress;
      document.getElementById('detailAddress')?.focus();
    }
  }).open();
};
</script>

<style scoped>
/* style.css의 변수 정의를 인라인 스타일에서 사용하기 위해 여기에 변수 선언 */
:root {
  --grad1: #4f9cf9;
  --grad2: #a78bfa;
}

</style>