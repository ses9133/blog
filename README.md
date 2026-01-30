# 🚀 My-Blog: 수익형 게시판 플랫폼

Spring Boot 기반의 통합 커뮤니티 플랫폼으로, **소셜 로그인, 이메일 인증, 포인트 결제, 유료 콘텐츠 구매 및 관리자 환불 프로세스**까지 실제 서비스 운영에 필요한 핵심 비즈니스 로직을 구현한 프로젝트입니다.


## 🛠 Tech Stack

- ### Backend
| Category | Technology |
| :--- | :--- |
| **Language** | <img src="https://img.shields.io/badge/Java%2017-007396?style=flat-square&logo=openjdk&logoColor=white"/> |
| **Framework** | <img src="https://img.shields.io/badge/Spring%20Boot%203.4.12-6DB33F?style=flat-square&logo=springboot&logoColor=white"/> |
| **ORM** | <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white"/> |
| **Build Tool** | <img src="https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white"/> |
| **Security** | <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white"/> |

- ### Database & API
| Category | Technology |
| :--- | :--- |
| **Production** | <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white"/> |
| **Dev/Test** | <img src="https://img.shields.io/badge/H2%20Database-003366?style=flat-square&logo=databricks&logoColor=white"/> |
| **Auth** | <img src="https://img.shields.io/badge/OAuth%202.0-3C3C3C?style=flat-square&logo=google&logoColor=white"/> |
| **API** | <img src="https://img.shields.io/badge/PortOne%20V2-000000?style=flat-square&logo=target&logoColor=white"/> |
| **Email** | <img src="https://img.shields.io/badge/Google%20SMTP-EA4335?style=flat-square&logo=gmail&logoColor=white"/>  |

- ### Frontend
| Category | Technology |
| :--- | :--- |
| **View** | <img src="https://img.shields.io/badge/Mustache-FF6100?style=flat-square&logo=mustache&logoColor=white"/> |
| **UI** | <img src="https://img.shields.io/badge/Bootstrap%205-7952B3?style=flat-square&logo=bootstrap&logoColor=white"/> |
| **Script** | <img src="https://img.shields.io/badge/Vanilla%20JS-F7DF1E?style=flat-square&logo=javascript&logoColor=black"/> |


## ✨ Key Features

### 1. 사용자 인증 및 보안 (Auth & Security)
- **통합 소셜 로그인**: 카카오와 네이버 로그인을 동시에 지원하며, 각 플랫폼의 프로필 이미지를 시스템 내 이미지로 자동 동기화합니다.
- **비밀번호 암호화**: BCrypt 해싱을 적용하여 사용자 개인정보를 안전하게 보호합니다.
- **RBAC (Role-Based Access Control)**: 인터셉터(Interceptor)와 엔티티 레벨의 인가 로직(`isOwner`)을 결합하여 관리자와 일반 사용자의 권한을 분리했습니다. 
- **이메일 인증 시스템**: `MailService`와 `MailUtil`을 통해 회원가입 시 실시간 인증 번호를 발송하고, 세션을 통한 본인 인증을 수행합니다.
- **프로필 관리**: UUID 기반의 파일 관리 시스템을 구축하여 파일 충돌을 방지하고, 최대 10MB의 멀티파트 업로드를 지원합니다.

### 2. 결제(포인트 충전) 및 게시글 구매 시스템 (Payment & Purchase)
- **2단계 결제 검증**: 
  - **준비**: 결제 전 서버에 주문 금액을 예약 저장하여 위변조의 토대를 만듭니다.
  - **검증**: 결제 완료 후 포트원 서버와의 통신으로 실제 결제 금액을 대조하여 금액 위변조를 원천 차단합니다.
- **트랜잭션 처리**: 콘텐츠 구매 시 포인트 차감과 읽기 권한 부여를 하나의 트랜잭션으로 묶어 데이터 원자성(Atomicity)을 보장했습니다.

### 3. 환불 플로우 (Refund Management)
- **워크플로우**: `PENDING → APPROVED/REJECTED`로 이어지는 주기로 설계했습니다.
- **사용자 환불 요청**: 결제 내역에서 사유와 함께 환불을 신청하면 `PENDING` 상태로 관리자에게 접수됩니다.
- **관리자 전용 대시보드**: 관리자가 요청 건을 검토하고, 거절 사유 입력 과 포트원 API 연동을 통한 즉시 환불 승인 처리를 수행합니다.
- **자동 포인트 회수**: 환불 승인 시 지급되었던 포인트가 자동으로 회수되며 결제 상태가 업데이트됩니다.

### 4. 커뮤니티 기능 (Board & Reply)
- **게시판 운영**: Spring Data JPA의 Pageable을 활용한 페이징 처리 및 검색을 포함한 CRUD를 제공합니다.
- **수익형 게시판**: 무료/프리미엄 게시글 속성을 구분하여, 구매 이력에 따른 동적 콘텐츠 노출 로직을 구현했습니다.
- **댓글**: 댓글 CRUD를 제공합니다.

### 5. 예외 처리 (Error Handling)
- **커스텀 예외 클래스**: Exception400, Exception401, Exception403, Exception404, Exception500
- **@ControllerAdvice**: : 전역에서 발생하는 예외를 한곳에서 가로채 관리하며, API 요청과 일반 페이지 요청을 구분하여 에러 응답을 제공합니다.


## 📂 Project Structure
도메인 중심의 계층형 아키텍처를 따릅니다.

```text
📦 blog
 ┣ 📂 .github
 ┃ ┗ 📂 ISSUE_TEMPLATE  
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java
 ┃ ┃ ┃ ┗ 📂 org.example.blog
 ┃ ┃ ┃   ┣ 📂 _core                 # 공통 기능 (설정, 예외, 인터셉터, 유틸)
 ┃ ┃ ┃   ┃ ┣ 📂 config              # WebMvcConfig 등 시스템 설정
 ┃ ┃ ┃   ┃ ┣ 📂 constants           # 세션 키 등 공통 상수
 ┃ ┃ ┃   ┃ ┣ 📂 errors              # 전역 예외 처리 및 커스텀 Exception
 ┃ ┃ ┃   ┃ ┣ 📂 interceptor         # 권한 및 세션 체크 인터셉터
 ┃ ┃ ┃   ┃ ┣ 📂 response            # 공통 API 응답 규격 (ApiResponse)
 ┃ ┃ ┃   ┃ ┗ 📂 utils               # 날짜, 파일, 메일 관련 유틸리티
 ┃ ┃ ┃   ┣ 📂 admin                 # 관리자 대시보드 및 환불 관리
 ┃ ┃ ┃   ┣ 📂 board                 # 게시판 (포스팅, 페이징, CRUD)
 ┃ ┃ ┃   ┣ 📂 payment               # 결제 (포트원 API 연동 및 검증)
 ┃ ┃ ┃   ┣ 📂 purchase              # 콘텐츠 구매 및 내역 관리
 ┃ ┃ ┃   ┣ 📂 refund                # 환불 요청 및 상태 처리
 ┃ ┃ ┃   ┣ 📂 reply                 # 댓글/답글 시스템
 ┃ ┃ ┃   ┗ 📂 user                  # 사용자 관리 (Local/Social Login)
 ┃ ┃ ┃     ┣ 📂 kakao               # 카카오 소셜 로그인 연동
 ┃ ┃ ┃     ┣ 📂 mail                # 이메일 인증 서비스
 ┃ ┃ ┃     ┗ 📂 naver               # 네이버 소셜 로그인 연동
 ┃ ┃ ┗ 📂 resources
 ┃ ┃ ┃ ┣ 📂 db                      # 초기 데이터 (data.sql)
 ┃ ┃ ┃ ┣ 📂 static                  # 정적 리소스 (CSS, JS, Images)
 ┃ ┃ ┃ ┣ 📂 templates               # Mustache 뷰 템플릿 (admin, board 등)
 ┃ ┃ ┃ ┗ 📜 application*.yml        # 환경별 설정 (dev, local, prod)
 ┃ ┗ 📂 test                        # 테스트 코드 (JUnit5)
 ┣ 📜 .env                          # 환경 변수 (API 키 및 보안 설정)
 ┣ 📜 pom.xml                       # Maven 의존성 관리 설정
 ┗ 📜 README.md                     # 프로젝트 문서

```

## 📔 ERD
<img width="650" height="500" alt="Gemini_Generated_Image_qm7h79qm7h79qm7h" src="https://github.com/user-attachments/assets/f7917597-29e1-405e-bbca-9d4c7034279a" />


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
| 카카오 로그인 | GET | `/auth/kakao` | OAuth2 |
| 카카오 로그인 콜백 | GET | `/auth/kakao/code` | OAuth2 |
| 네이버 로그인 콜백 | GET | `/auth/naver/code` | OAuth2 |
| 마이페이지 | GET | `/me` | |
| 프로필 수정 폼| GET | `/me/edit` | 이미지 업로드 포함 |
| 프로필 수정 처리| PUT | `/api/v1/me` | 이미지 업로드 포함 |
| 프로필 이미지 삭제 | DELETE | `/me/profile-image` | |

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
KAKAO_REDIRECT_URI=http://localhost:8080/auth/kakao
NAVER_CLIENT_ID=your_naver_client_id
NAVER_CLIENT_SECRET=your_naver_client_secret
NAVER_REDIRECT_URI=http://localhost:8080/auth/naver

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
