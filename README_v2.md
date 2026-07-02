# IT211 Course Management System - README v2

## Mô tả dự án
Hệ thống quản lý khóa học (Course Management) xây dựng bằng **Spring Boot 4.1** (Backend REST API) và **React + TypeScript + Vite** (Frontend SPA).

---

## Yêu cầu hệ thống

| Phần mềm | Phiên bản tối thiểu | Ghi chú |
|----------|---------------------|---------|
| Java (JDK) | 21+ | Khuyến nghị: OpenJDK 21 |
| MySQL | 8.0+ | Database chính |
| Node.js | 18+ | Frontend dev server |
| npm | 9+ | Package manager |

---

## Cấu hình Backend

### 1. Tạo database MySQL

```sql
CREATE DATABASE course_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Cấu hình file `.env`

Tạo file `Backend/backend/.env` với nội dung:

```properties
JWT_SECRET=<your-base64-encoded-secret-key>

MYSQL_USERNAME=root
MYSQL_PASSWORD=<your-mysql-password>

CLOUDINARY_CLOUD_NAME=<your-cloudinary-cloud-name>
CLOUDINARY_API_KEY=<your-cloudinary-api-key>
CLOUDINARY_API_SECRET=<your-cloudinary-api-secret>
```

### 3. Token blacklist dùng MySQL

Project hiện lưu token blacklist trong bảng `token_blacklist` của MySQL (JPA), không cần Redis runtime.

---

## Chạy dự án

### Backend

```bash
cd Backend/backend

# Chạy backend (cần MySQL đang chạy)
./gradlew bootRun
```

Backend sẽ chạy tại: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend

```bash
cd Frontend

# Cài dependencies (lần đầu)
npm install

# Chạy dev server
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`

---

## Chạy Unit Tests

```bash
cd Backend/backend

# Chạy tất cả unit tests
./gradlew test

# Xem report (HTML)
# Mở file: Backend/backend/build/reports/tests/test/index.html
```

**Lưu ý:** Unit tests sử dụng H2 in-memory database và mock Redis, nên **không cần** MySQL hay Redis đang chạy để chạy tests.

---

## Cấu trúc dự án

```
it211_project/
├── Backend/
│   ├── backend/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/example/backend/
│   │   │   │   │   ├── aop/              # FR-11: AOP Logging
│   │   │   │   │   ├── config/           # Security, Redis, Cloudinary
│   │   │   │   │   ├── controller/       # REST Controllers
│   │   │   │   │   ├── dto/              # Request/Response DTOs
│   │   │   │   │   ├── entity/           # JPA Entities
│   │   │   │   │   ├── exception/        # Global Exception Handler
│   │   │   │   │   ├── repository/       # JPA Repositories
│   │   │   │   │   ├── security/         # JWT, Auth Filter
│   │   │   │   │   ├── service/          # Business Logic + Redis Blacklist
│   │   │   │   │   └── util/
│   │   │   │   └── resources/
│   │   │   └── test/                     # FR-12: Unit Tests (H2)
│   │   └── build.gradle
│   └── db/
├── Frontend/
│   ├── src/
│   │   ├── api/                          # Axios clients
│   │   ├── context/                      # Auth Context
│   │   ├── pages/                        # Admin, Lecturer, Student, Auth
│   │   ├── routes/                       # Protected Routes
│   │   └── types/                        # TypeScript interfaces
│   └── package.json
└── docs/
```

---

## Trạng thái chức năng theo rubric chấm điểm

| Mã | Yêu cầu | Trạng thái hiện tại | Ghi chú |
|----|---------|---------------------|---------|
| FR-01 | Đăng nhập hệ thống (JWT) | Đạt | Login API + frontend hoạt động |
| FR-02 | Xoay vòng Token (Refresh Token) | Một phần | Có refresh token và auto-refresh, nhưng chưa revoke refresh token cũ theo cơ chế rotate chặt |
| FR-03 | Đăng xuất (Revoke Token) | Một phần | Đã blacklist access token; chưa revoke refresh token khi logout |
| FR-04 | Đăng ký tài khoản Sinh viên mới | Đạt | Register tạo user role STUDENT |
| FR-05 | Quản lý Người dùng & Lớp học (CRUD, tìm kiếm, phân trang) | Một phần | Backend đủ CRUD + search + paging; frontend chưa có màn hình update |
| FR-06 | Đăng ký tham gia khóa học (Student) | Đạt | Có enroll/cancel + danh sách khóa học và enrollments |
| FR-07 | Nộp bài tập/đồ án (GitHub hoặc File) | Một phần | Luồng nộp bài có hoạt động, nhưng hiện yêu cầu cả GitHub URL và file |
| FR-08 | Chấm điểm + feedback (Lecturer) | Đạt | Có API và UI nhập điểm/nhận xét |
| FR-09 | Tải lên tài liệu bài giảng (Lecturer) | Đạt | Có upload/list material cho lecturer và student xem |
| FR-10 | Đổi mật khẩu / Quên mật khẩu | Một phần | Backend đã có endpoint; frontend chưa có màn hình forgot/reset/change password |
| FR-11 | Ghi log thời gian thực hiện cho tất cả chức năng | Đạt | AOP @Around cho toàn bộ controller/service |
| FR-12 | Unit test tối thiểu 10 test (5 service, 5 controller) | Một phần | Đủ 10 test case trong code, cần xác nhận build/test pass ổn định trước khi nộp |
| FR-13 | Dùng Redis cho TokenBlacklist | Chưa áp dụng | Đang dùng MySQL token_blacklist |

---

## Ước tính điểm hiện tại (tham khảo)

- Nếu chấm nghiêm theo đúng rubric: khoảng 85-95 điểm.
- Nếu hoàn tất các mục còn thiếu bên dưới: có thể tiến gần/full 100.

---

## Checklist cần hoàn tất trước khi nộp

1. FR-02/FR-03: triển khai rotate refresh token đầy đủ và revoke cả refresh token khi logout.
2. FR-07: chỉnh logic nộp bài đúng yêu cầu "GitHub hoặc File" (không bắt buộc đồng thời cả hai).
3. FR-10: bổ sung UI cho forgot password, reset password, change password.
4. FR-05: bổ sung chức năng update user/course ở frontend để khớp đầy đủ CRUD.
5. FR-12: chạy lại test và lưu bằng chứng:
   - `cd Backend/backend`
   - `./gradlew clean test` (hoặc `gradlew.bat clean test` trên Windows)
   - đính kèm report `build/reports/tests/test/index.html` khi nộp.

---

## Tính năng nâng cao đã có

### FR-11: AOP Logging
- Có `LoggingAspect` cho controller/service.
- Log tên method, thời gian chạy (ms), trạng thái SUCCESS/FAILED.

### FR-12: Unit Tests
- Có đủ 10 test case trong mã nguồn: 5 service + 5 controller.
- Cần đảm bảo toàn bộ test pass trong môi trường nộp.

### FR-13: Redis TokenBlacklist
- Chưa áp dụng trong phiên bản hiện tại.
- Phiên bản hiện tại lưu token blacklist bằng MySQL (`token_blacklist`).

---

## Tài khoản mặc định

Sau khi chạy, tạo tài khoản admin qua API hoặc SQL:

```sql
INSERT INTO users (full_name, email, password, role, enabled, created_at, updated_at)
VALUES ('Admin', 'admin@example.com', '$2a$12$...', 'ADMIN', true, NOW(), NOW());
```

Hoặc đăng ký tài khoản Student qua Frontend: `http://localhost:5173/auth/register`

---

## Troubleshooting

### MySQL connection refused
→ Kiểm tra MySQL đã chạy và database `course_management` đã tồn tại

### Tests fail
```bash
# Chạy test với log chi tiết
./gradlew test --info
```
Tests dùng H2 + mock Redis, không cần external services.
