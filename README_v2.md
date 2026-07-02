# IT211 Course Management System - README v2 (Rubric Submission)

## 1. Purpose
README nay dung de nop bai va doi chieu nhanh theo rubric chuc nang.

## 2. Tech Stack
- Backend: Spring Boot 4.1, Spring Security, Spring Data JPA
- Frontend: React + TypeScript + Vite
- Database: MySQL
- File storage: Cloudinary
- Auth: JWT access/refresh + token blacklist

## 3. Run Quick
### 3.1 Prerequisites
- Java 21+
- MySQL 8+
- Node.js 18+, npm 9+

### 3.2 Database
```sql
CREATE DATABASE IF NOT EXISTS course_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

### 3.3 Environment
Tao file `Backend/backend/.env`:
```properties
JWT_SECRET=<base64-secret>
MYSQL_USERNAME=root
MYSQL_PASSWORD=<password>
CLOUDINARY_CLOUD_NAME=<cloud-name>
CLOUDINARY_API_KEY=<api-key>
CLOUDINARY_API_SECRET=<api-secret>
```

### 3.4 Start backend
```bash
cd Backend/backend
gradlew.bat bootRun
```

### 3.5 Start frontend
```bash
cd Frontend
npm install
npm run dev
```

## 4. Rubric Matrix
| Code | Requirement | Status | Note |
|------|-------------|--------|------|
| FR-01 | Login + JWT | Dat | Hoat dong day du |
| FR-02 | Refresh token rotation | Mot phan | Co refresh, chua rotate/revoke refresh token cu |
| FR-03 | Logout + revoke token | Mot phan | Revoke access token, chua revoke refresh token |
| FR-04 | Student register | Dat | Register role STUDENT |
| FR-05 | Admin user/course CRUD + search + paging | Dat | Frontend da co edit inline user/course |
| FR-06 | Student enroll course | Dat | Enroll/cancel/list |
| FR-07 | Submit assignment (GitHub or file) | Mot phan | Hien dang can ca githubUrl va file |
| FR-08 | Lecturer grading + feedback | Dat | Da co list submissions + grade |
| FR-09 | Lecturer upload materials | Dat | Upload/list/update/delete |
| FR-10 | Change/forgot password | Mot phan | Backend co endpoint, frontend chua co man hinh |
| FR-11 | Execution time logging | Dat | AOP around controller/service |
| FR-12 | Unit test >=10 (5 service + 5 controller) | Dat dieu kien toi thieu | Da co 10 test case, can nop report test |
| FR-13 | Redis token blacklist | Khong ap dung trong branch nay | Da doi sang MySQL blacklist theo yeu cau hien tai |

## 5. Remaining 3 TODO (Priority)
1. FR-02/FR-03: Rotate refresh token dung chuan va revoke refresh token khi logout.
2. FR-07: Cho phep submit "GitHub hoac File" thay vi bat buoc ca hai.
3. FR-10: Them UI cho forgot/reset/change password.

## 6. Evidence Pointers
- Auth + blacklist: `Backend/backend/src/main/java/com/example/backend/service/AuthService.java`
- Blacklist persistence: `Backend/backend/src/main/java/com/example/backend/entity/TokenBlacklist.java`
- Admin management UI: `Frontend/src/pages/admin/UserManagementPage.tsx`
- Lecturer assignment flow: `Frontend/src/pages/lecturer/LecturerDashboardPage.tsx`
- Student submission flow: `Frontend/src/pages/student/SubmissionPage.tsx`

## 7. Pre-submission Checklist
- Run backend compile: `gradlew.bat classes`
- Run frontend build: `npm run build`
- Run unit tests: `gradlew.bat test`
- Attach test report: `Backend/backend/build/reports/tests/test/index.html`
