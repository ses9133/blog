# 🚀 My-Blog-Service: 수익형 커뮤니티 플랫폼

Spring Boot 기반의 통합 커뮤니티 플랫폼으로, **소셜 로그인, 이메일 인증, 포인트 결제, 유료 콘텐츠 구매 및 관리자 환불 프로세스**까지 실제 서비스 운영에 필요한 핵심 비즈니스 로직을 구현한 프로젝트입니다.


## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.4.12, Spring Data JPA
- **Build Tool**: Maven
- **Database**: MySQL / H2 (Development)
- **Security & Auth**: 
  - **OAuth 2.0**: Kakao & Naver Social Login
  - **Email**: Google SMTP 기반 메일 인증 시스템
  - **Encryption**: BCrypt Password Encoding
- **API Integration**: PortOne V2 (결제/취소 API)
- **Frontend**: Mustache, Bootstrap 5, Vanilla JS (AJAX)


## ✨ Key Features

### 1. 🔐 사용자 인증 및 보안 (Auth & Security)
- **통합 소셜 로그인**: 카카오와 네이버 로그인을 동시에 지원하며, 각 플랫폼의 프로필 이미지를 시스템 내 이미지로 자동 동기화합니다.
- **비밀번호 암호화**: BCrypt 해싱을 적용하여 사용자 개인정보를 안전하게 보호합니다.
- **RBAC (Role-Based Access Control)**: 인터셉터(Interceptor)와 엔티티 레벨의 인가 로직(`isOwner`)을 결합하여 관리자와 일반 사용자의 권한을 분리했습니다. 
- **이메일 인증 시스템**: `MailService`와 `MailUtil`을 통해 회원가입 시 실시간 인증 번호를 발송하고, 세션을 통한 본인 인증을 수행합니다.
- **프로필 관리**: UUID 기반의 파일 관리 시스템을 구축하여 파일 충돌을 방지하고, 최대 10MB의 멀티파트 업로드를 지원합니다.

### 2. 💰 결제(포인트 충전) 및 게시글 구매 시스템 (Payment & Purchase)
- **2단계 결제 검증**: 
  - **준비**: 결제 전 서버에 주문 금액을 예약 저장하여 위변조의 토대를 만듭니다.
  - **검증**: 결제 완료 후 포트원 서버와의 통신으로 실제 결제 금액을 대조하여 금액 위변조를 원천 차단합니다.
- **트랜잭션 처리**: 콘텐츠 구매 시 포인트 차감과 읽기 권한 부여를 하나의 트랜잭션으로 묶어 데이터 원자성(Atomicity)을 보장했습니다.

### 3. 🔄 환불 플로우 (Refund Management)
- **워크플로우**: `PENDING → APPROVED/REJECTED`로 이어지는 주기로 설계했습니다.
- **사용자 환불 요청**: 결제 내역에서 사유와 함께 환불을 신청하면 `PENDING` 상태로 관리자에게 접수됩니다.
- **관리자 전용 대시보드**: 관리자가 요청 건을 검토하고, 거절 사유 입력 과 포트원 API 연동을 통한 즉시 환불 승인 처리를 수행합니다.
- **자동 포인트 회수**: 환불 승인 시 지급되었던 포인트가 자동으로 회수되며 결제 상태가 업데이트됩니다.

### 📝 4. 커뮤니티 기능 (Board & Reply)
- **게시판 운영**: Spring Data JPA의 Pageable을 활용한 페이징 처리 및 검색을 포함한 CRUD를 제공합니다.
- **수익형 게시판**: 무료/프리미엄 게시글 속성을 구분하여, 구매 이력에 따른 동적 콘텐츠 노출 로직을 구현했습니다.
- **댓글**: 댓글 CRUD를 제공합니다.

### ⚒️ 5. 예외 처리 (Error Handling)
- **커스텀 예외 클래스**: Exception400, Exception401, Exception403, Exception404, Exception500
- **@ControllerAdvice**: : 전역에서 발생하는 예외를 한곳에서 가로채 관리하며, API 요청과 일반 페이지 요청을 구분하여 에러 응답을 제공합니다.


## 📂 Project Structure

```text
src/main/java/org/example/blog
├── _core       # 공통 예외 처리, 응답 DTO, 유틸리티(Mail, File)
├── admin       # 관리자 전용 대시보드 및 환불 관리 로직
├── board       # 게시글 관리 및 프리미엄 콘텐츠 서비스
├── payment     # 결제 예약/검증 및 PortOne API 연동
├── purchase    # 콘텐츠 구매 이력 및 권한 확인
├── refund      # 환불 요청 및 관리자 승인/취소 라이프사이클
├── reply       # 비동기 댓글 시스템
└── user        # 로그인(Kakao, Naver), 이메일 인증, 포인트 관리
```


## 🔗 URL Mapping 

### 👤 User & Auth (사용자 및 인증)
| 기능 | Method | URL | 비고 |
|------|--------|-----|------|
| 회원가입 폼 | GET | `/join` | |
| 회원가입 처리 | POST | `/join` | |
| 로그인 폼 | GET | `/login` | |
| 로그인 처리 | POST | `/login` | |
| 로그아웃 | GET | `/logout` | |
| 이메일 인증 발송 | POST | `/api/v1/email/send` |  |
| 이메일 인증 확인 | POST | `/api/v1/email/verify` |  |
| 카카오 로그인 | GET | `/login/oauth2/code/kakao` | OAuth2 |
| 네이버 로그인 | GET | `/login/oauth2/code/naver` | OAuth2 |
| 마이페이지 | GET | `/me/update-form` | |
| 프로필 수정 | POST | `/me/update` | 이미지 업로드 포함 |
| 프로필 이미지 삭제 | POST | `/me/profile-image/delete` | |

### 💰 Payment & Purchase (결제 및 구매)
| 기능 | Method | URL | 비고 |
|------|--------|-----|------|
| 포인트 충전 페이지 | GET | `/me/points/charge` | |
| 결제 사전 준비 | POST | `/api/v1/payment/prepare` | 위변조 방지 |
| 결제 사후 검증 | POST | `/api/v1/payment/verify` | 충전 완료 |
| 내 결제 내역 조회 | GET | `/me/payments` | |
| 프리미엄 게시글 구매 | POST | `/boards/{boardId}/purchases` | 포인트 차감 |
| 내 콘텐츠 구매 내역 | GET | `/me/purchases` | |

### 🔄 Refund (환불 프로세스)
| 기능 | Method | URL | 비고 |
|------|--------|-----|------|
| 환불 요청 폼 | GET | `/refunds/{paymentId}` | |
| 환불 요청 접수 | POST | `/refunds` | `PENDING` 상태 |
| 내 환불 내역 조회 | GET | `/refunds` | |

### 📝 Board & Reply (게시판 및 댓글)
| 기능 | Method | URL | 비고 |
|------|--------|-----|------|
| 게시글 목록 (메인) | GET | `/` 또는 `/boards` | 페이징 포함 |
| 게시글 작성 폼 | GET | `/boards/new` | |
| 게시글 저장 | POST | `/boards` | |
| 게시글 상세 조회 | GET | `/boards/{id}` | 구매 여부 체크 |
| 게시글 수정 폼 | GET | `/boards/{id}/update-form` | 작성자 확인 |
| 게시글 수정 완료 | PUT | `/api/v1/boards/{id}` |  |
| 게시글 삭제 | DELETE | `/api/v1/boards/{id}` |  |
| 댓글 등록 | POST | `/replies` | |
| 게시글별 댓글 목록 | GET | `/board/{boardId}/replies` | |
| 댓글 삭제 | DELETE | `/api/v1/replies/{replyId}` | |

### 👑 Admin (관리자 전용)
| 기능 | Method | URL | 비고 |
|------|--------|-----|------|
| 관리자 대시보드 | GET | `/admin/dashboard` | |
| 전체 환불 요청 목록 | GET | `/admin/refunds` | |
| 환불 요청 승인 | POST | `/admin/refunds/{id}/approve` | PortOne API 취소 연동 |
| 환불 요청 거절 | POST | `/admin/refunds/{id}/reject` | 거절 사유 입력 필수 |

## 🚀 How To Run

### 1. 환경 변수 설정 (.env)
```env
# Database (MySQL 사용 시 해당 계정 입력)
DB_USERNAME=your_username
DB_PASSWORD=your_password

# OAuth Configuration
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=http://localhost:8080/user/kakao
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
NAVER_REDIRECT_URI=http://localhost:8080/user/naver

# Social & PortOne API
SOCIAL_KEY=your_random_social_key
IMP_REST_API_KEY=your_portone_key
IMP_SECRET_KEY=your_portone_secret

# Mail Service (Google SMTP)
GMAIL_USERNAME=your_gmail_account@gmail.com
GMAIL_APP_PASSWORD=your_google_app_password
```

### 2. 빌드 및 실행
```
mvn clean install
mvn spring-boot:run
```