# SmartLab Management System - Backend API Base

Dự án backend xây dựng trên nền tảng **Spring Boot 3 (Java 21)**, tích hợp **Spring Security 6 + JWT**, cấu hình **Redis Cache / Token Blacklist**, tài liệu hóa **Swagger / OpenAPI 3**, và kết nối cơ sở dữ liệu **PostgreSQL** dựa trên schema [dtb.sql](file:///c:/Users/ttrrang/Documents/TaiSEP/dtb.sql).

---

## 1. Phân Tích Cơ Sở Dữ Liệu `dtb.sql`

File [dtb.sql](file:///c:/Users/ttrrang/Documents/TaiSEP/dtb.sql) định nghĩa hệ thống quản lý SmartLab hoàn chỉnh bao gồm 31 bảng và 3 views:

1. **Phân hệ Core / User & Xác thực**:
   - `users`: Bảng trung tâm lưu trữ người dùng hệ thống.
     - `id`: Định danh `bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY`.
     - `org_email`: Email tổ chức (duy nhất, dùng để đăng nhập).
     - `password_hash`: Mật khẩu mã hóa BCrypt.
     - `full_name`, `date_of_birth`, `department`, `note`, `avatar_url`, `phone`.
     - `role`: Vai trò toàn cục với CHECK: `'it_admin'`, `'business_admin'`, `'manager'`, `'leader'`, `'member'`, `'guest'`.
     - `status`: Trạng thái tài khoản với CHECK: `'active'`, `'inactive'`, `'locked'`.
     - `created_at`, `updated_at`, `deleted_at`: Quản lý thời gian và soft-delete.
   - `auth_verification_codes`: Lưu mã xác thực khôi phục mật khẩu.
   - `system_settings`: Cấu hình hệ thống key-value.
   - `notification_settings`: Cấu hình bật/tắt các loại thông báo.
2. **Phân hệ Danh mục & Dự án**:
   - `domains`: Lĩnh vực nghiên cứu.
   - `project_types`: Phân loại dự án.
   - `projects`: Thông tin dự án, tiến độ, thời hạn, trạng thái (`pending`, `processing`, `done`, `cancel`).
   - `project_members`: Thành viên tham gia dự án với vai trò (`owner`, `coordinator`, `leader`, `member`).
3. **Phân hệ Công việc (Task)**:
   - `tasks`: Quản lý task đa cấp (tối đa 3 cấp qua `parent_task_id`), trạng thái (`pending`, `processing`, `pending_review`, `done`, `rejected`, `cancel`).
   - `task_assignees`: Phân công công việc (N-N).
   - `task_comments`: Bình luận thảo luận.
   - `task_logs`: Nhật ký kiểm toán (audit trail).
   - `task_logworks`: Bảng chấm công / ghi nhận thời gian làm việc thực tế.
4. **Phân hệ Tin tức & Bài viết**:
   - `news`: Tin tức công khai / nội bộ.
   - `posts`, `post_attachments`, `post_comments`: Bài đăng thảo luận cấp Lab và Dự án.
5. **Phân hệ Tuyển dụng**:
   - `recruitment_posts`, `recruitment_form_fields`, `applications`, `interviews`.
6. **Phân hệ Tài liệu & Thông báo**:
   - `folders`, `materials`, `notifications`, `report_exports`.
7. **Phân hệ Chat Realtime**:
   - `chat_conversations`, `chat_participants`, `chat_messages`, `chat_message_attachments`, `chat_message_reactions`, `chat_message_mentions`.
   - Views: `v_chat_user_conversations`, `v_chat_messages`, `v_chat_members`.

---

## 2. Công Nghệ & Cấu Trúc Dự Án

- **Ngôn ngữ**: Java 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Bảo mật**: Spring Security 6, JJWT 0.12.6, BCrypt Password Encoder
- **Database**: PostgreSQL Driver + Spring Data JPA (Hibernate 6)
- **Caching & Session**: Spring Data Redis (Lettuce), RedisTemplate với GenericJackson2JsonRedisSerializer (hỗ trợ Java 8 Date/Time)
- **Tài liệu API**: Springdoc OpenAPI 2.6.0 (Swagger UI 5)
- **Tiện ích**: Lombok, Validation API

### Cấu trúc thư mục:
```
src/main/java/com/smartlab/
├── SmartLabApplication.java              # Main Application Entry Point
├── common/
│   └── response/
│       └── ApiResponse.java              # Chuẩn hóa format response JSON
├── config/
│   ├── SecurityConfig.java               # Cấu hình Spring Security, CORS, Filter Chain
│   ├── RedisConfig.java                  # Cấu hình Lettuce, RedisTemplate, CacheManager
│   └── OpenApiConfig.java                # Cấu hình Swagger UI + Bearer Auth
├── controller/
│   ├── AuthController.java               # API Đăng nhập, Profile, Đăng xuất
│   └── UserController.java               # API Quản lý thông tin User Profile
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java             # Request đăng nhập
│   │   └── UpdateProfileRequest.java     # Request cập nhật profile
│   └── response/
│       ├── LoginResponse.java            # Response đăng nhập (Token + User info)
│       └── UserProfileResponse.java      # Response thông tin chi tiết người dùng
├── entity/
│   ├── User.java                         # Entity ánh xạ bảng public.users
│   ├── Role.java                         # Enum vai trò (it_admin, manager, leader, member...)
│   └── UserStatus.java                   # Enum trạng thái (active, inactive, locked)
├── exception/
│   ├── AppException.java                 # Base exception
│   ├── ResourceNotFoundException.java    # 404 Exception
│   ├── BadRequestException.java          # 400 Exception
│   └── GlobalExceptionHandler.java       # Bắt lỗi toàn cục và trả về JSON chuẩn
├── init/
│   └── DataInitializer.java              # Tự động khởi tạo tài khoản mẫu nếu DB chưa có
├── repository/
│   └── UserRepository.java               # Spring Data JPA Repository
├── security/
│   ├── CustomUserDetails.java            # UserDetails của Spring Security
│   ├── CustomUserDetailsService.java     # Service nạp user từ Database
│   ├── JwtTokenProvider.java             # Sinh mã, phân tích và xác thực JWT
│   ├── JwtAuthenticationFilter.java      # Filter xác thực Bearer token và kiểm tra Redis
│   ├── JwtAuthenticationEntryPoint.java  # Xử lý phản hồi lỗi 401 Unauthorized
│   └── CustomAccessDeniedHandler.java    # Xử lý phản hồi lỗi 403 Forbidden
└── service/
    ├── AuthService.java                  # Interface dịch vụ xác thực
    ├── UserService.java                  # Interface dịch vụ người dùng
    ├── RedisTokenService.java            # Quản lý Blacklist token & Cache profile trên Redis
    └── impl/
        ├── AuthServiceImpl.java          # Thực hiện đăng nhập, đăng xuất
        └── UserServiceImpl.java          # Thực hiện lấy & cập nhật profile
```

---

## 3. Cấu Hình Ứng Dụng (`application.yml`)

Thông tin kết nối CSDL và Redis đã được cấu hình sẵn theo yêu cầu:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://103.124.94.55:5432/smartlab}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:0146424Minh}
    driver-class-name: org.postgresql.Driver

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

app:
  jwt:
    secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    expiration-ms: ${JWT_EXPIRATION_MS:86400000} # 24 giờ
```

---

## 4. Tài Khoản Mẫu Có Sẵn (Auto Seeded)

Khi ứng dụng khởi động, nếu bảng `public.users` chưa có các tài khoản này, `DataInitializer` sẽ tự động tạo:

| Email | Mật khẩu | Họ và tên | Vai trò (`role`) | Trạng thái (`status`) |
| :--- | :--- | :--- | :--- | :--- |
| `admin@smartlab.vn` | `Admin@123` | System Administrator | `it_admin` | `active` |
| `manager@smartlab.vn` | `Manager@123` | Lab Manager | `manager` | `active` |
| `member@smartlab.vn` | `Member@123` | Lab Member | `member` | `active` |

---

## 5. Tài Liệu 2 API Chính & Swagger

### Swagger UI:
Truy cập tài liệu trực quan tại trình duyệt:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**
👉 OpenAPI JSON Docs: **[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)**

---

### 🔑 API 1: Đăng nhập (`Login API`)

- **URL**: `POST /api/v1/auth/login`
- **Quyền truy cập**: Public (Không yêu cầu token)
- **Mô tả**: Xác thực người dùng bằng `orgEmail` và `password`. Kiểm tra tài khoản không bị xóa, không bị khóa (`locked`) hoặc ngưng hoạt động (`inactive`). Tạo JWT token và lưu cache thông tin vào Redis.

#### Request Body (`application/json`):
```json
{
  "orgEmail": "admin@smartlab.vn",
  "password": "Admin@123"
}
```

#### cURL:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/login" \
     -H "Content-Type: application/json" \
     -d "{\"orgEmail\": \"admin@smartlab.vn\", \"password\": \"Admin@123\"}"
```

#### Response Thành công (`200 OK`):
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBzbWFydGxhYi52biIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "user": {
      "id": 1,
      "orgEmail": "admin@smartlab.vn",
      "fullName": "System Administrator",
      "dateOfBirth": "1995-05-20",
      "department": "IT Administration",
      "note": "Default System Administrator account",
      "avatarUrl": "https://ui-avatars.com/api/?name=Admin+SmartLab&background=0D8ABC&color=fff",
      "phone": "0987654321",
      "role": "it_admin",
      "status": "active",
      "createdAt": "2026-10-03 09:00:26",
      "updatedAt": "2026-10-03 09:00:26"
    }
  },
  "timestamp": "2026-10-03 09:00:27"
}
```

---

### 👤 API 2: Lấy thông tin Profile (`Profile API`)

- **URL**: `GET /api/v1/auth/profile` *(hoặc `GET /api/v1/users/profile`)*
- **Quyền truy cập**: Yêu cầu xác thực (`Bearer <accessToken>`)
- **Mô tả**: Trích xuất danh tính từ JWT token, kiểm tra trạng thái hoạt động của tài khoản, kiểm tra token trong Redis blacklist, lấy dữ liệu profile đầy đủ từ Redis Cache (hoặc PostgreSQL nếu cache chưa có) và trả về.

#### Headers:
```http
Authorization: Bearer <accessToken>
```

#### cURL:
```bash
curl -X GET "http://localhost:8080/api/v1/auth/profile" \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

#### Response Thành công (`200 OK`):
```json
{
  "success": true,
  "statusCode": 200,
  "message": "Profile retrieved successfully",
  "data": {
    "id": 1,
    "orgEmail": "admin@smartlab.vn",
    "fullName": "System Administrator",
    "dateOfBirth": "1995-05-20",
    "department": "IT Administration",
    "note": "Default System Administrator account",
    "avatarUrl": "https://ui-avatars.com/api/?name=Admin+SmartLab&background=0D8ABC&color=fff",
    "phone": "0987654321",
    "role": "it_admin",
    "status": "active",
    "createdAt": "2026-10-03 09:00:26",
    "updatedAt": "2026-10-03 09:00:26"
  },
  "timestamp": "2026-10-03 09:00:27"
}
```

#### Response khi không truyền Token hoặc Token sai (`401 Unauthorized`):
```json
{
  "success": false,
  "statusCode": 401,
  "message": "Unauthorized: Full authentication is required to access this resource",
  "data": null,
  "timestamp": "2026-10-03 09:00:27"
}
```

---

### 🛡️ Các API Bổ Sung:

1. **Đăng xuất (`POST /api/v1/auth/logout`)**:
   - Đưa JWT token hiện tại vào Blacklist của Redis với thời gian hết hạn tương ứng thời gian còn lại của token.
   - Khi token đã bị Blacklist, filter bảo mật sẽ chặn mọi request tiếp theo dùng token này ngay cả khi token chưa hết hạn về mặt thời gian.
2. **Cập nhật Profile (`PUT /api/v1/users/profile`)**:
   - Cho phép người dùng cập nhật `fullName`, `phone`, `dateOfBirth`, `department`, `note`, `avatarUrl`.
   - Tự động xóa và cập nhật cache mới trên Redis.

---

## 6. Hướng Dẫn Chạy Ứng Dụng

### Chạy bằng Maven Wrapper:
```powershell
.\mvnw.cmd spring-boot:run
```

### Chạy bằng Maven thông thường:
```powershell
mvn clean spring-boot:run
```

### Chạy Test kiểm tra toàn bộ luồng:
```powershell
mvn test
```
