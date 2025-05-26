# 카페 주문 서비스 API 명세서
해당 문서는 수동 업데이트를 통해 만들어진 것으로 최신본이 아닐 수 있습니다. 
정확한 API 명세는 서버를 구동시켜 스웨거 UI를 확인해주세요.  

스웨거 UI 경로: `/swagger-ui/index.html`

## 인증
모든 API는 `x-user-id` 헤더를 통한 사용자 인증이 필요합니다. (회원가입 제외)

```
x-user-id: {사용자ID}
```

---

## 📋 목차
1. [회원 관리](#회원-관리)
2. [상품 주문 및 주문 취소](#상품-주문-및-주문-취소)
3. [데이터 모델](#데이터-모델)

---

## 회원 관리

### 1. 회원 가입
회원 가입을 진행합니다. 동일한 전화번호로 이중 가입할 수 없습니다.

**요청**
```
POST /api/public/user/register
Content-Type: application/json
```

**요청 예시**
```json
{
  "name": "홍길동",
  "phoneNumber": "010-1234-5678",
  "gender": "MALE",
  "birthDate": "1990-01-01"
}
```

**응답**
```json
{
  "userId": "1"
}
```

**필드 설명**
- `name`: 이름 (필수)
- `phoneNumber`: 전화번호 (필수)
- `gender`: 성별 - `MALE` 또는 `FEMALE` (필수)
- `birthDate`: 생년월일 - YYYY-MM-DD 형식 (필수)

---

### 2. 회원 탈퇴
회원 탈퇴를 진행합니다. 탈퇴 후 30일 이내에는 탈퇴 철회가 가능합니다.

**요청**
```
DELETE /api/user/withdraw
x-user-id: {사용자ID}
```

**응답**
```json
{
  "userId": "1"
}
```

---

### 3. 회원 탈퇴 철회
탈퇴를 철회합니다. 탈퇴 후 30일 이내에만 가능합니다.

**요청**
```
POST /api/user/revoke-withdrawal
x-user-id: {사용자ID}
```

**응답**
```json
{
  "userId": "1"
}
```

---

## 상품 주문 및 주문 취소

### 1. 주문 및 결제 준비
상품을 선택하고 주문을 준비합니다. 실제 결제가 이루어지기 전 요청합니다.
상품 id는 1,2,3으로 고정되어있으므로, 이들 중 선택해주세요.

**요청**
```
POST /api/order/prepare
Content-Type: application/json
x-user-id: {사용자ID}
```

**요청 예시**
```json
{
  "orderItems": [
    {
      "productId": "1",
      "quantity": 2
    },
    {
      "productId": "2", 
      "quantity": 1
    }
  ]
}
```

**응답**
```json
{
  "orderId": "123"
}
```

**필드 설명**
- `orderItems`: 주문할 상품 목록
  - `productId`: 상품 ID
  - `quantity`: 주문 수량

---

### 2. 상품 주문 (결제)
상품을 주문합니다. 결제 실패 시 주문도 취소됩니다.

**요청**
```
POST /api/order/{orderId}/pay
x-user-id: {사용자ID}
```

**경로 매개변수**
- `orderId`: 주문 ID (예: 1)

**응답**
```json
{
  "status": "SUCCESS",
  "orderId": "123",
  "message": "주문이 성공적으로 완료되었습니다."
}
```

**상태 코드**
- `SUCCESS`: 주문 성공
- `FAILED`: 주문 실패

---

### 3. 상품 주문 취소
상품 주문을 취소합니다. 결제도 함께 취소됩니다.

**요청**
```
POST /api/order/{orderId}/refund
x-user-id: {사용자ID}
```

**경로 매개변수**
- `orderId`: 주문 ID (예: 1)

**응답**
```json
{
  "status": "SUCCESS",
  "orderId": "123",
  "message": "주문이 성공적으로 취소되었습니다."
}
```


