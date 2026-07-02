# IT211 Course Management System - README v4 (Operations, Demo, and Troubleshooting)

## 1. Goal
README v4 dung cho van hanh local, demo voi giang vien, va xu ly loi nhanh.

## 2. Local Deployment Runbook
### 2.1 Start order
1. MySQL
2. Backend (Spring Boot)
3. Frontend (Vite)

### 2.2 Database initialization
- Chay script schema: `Backend/db/schema.sql`
- Chay sample data: `Backend/db/sample-data.sql`

### 2.3 Backend startup
```bash
cd Backend/backend
gradlew.bat bootRun
```
Expected log:
- App starts on port `8080`
- Security filter initialized
- JPA entities loaded

### 2.4 Frontend startup
```bash
cd Frontend
npm install
npm run dev
```
Expected URL:
- `http://localhost:5173`

## 3. Demo Script (Suggested)
### 3.1 Admin demo
1. Login as admin.
2. Open user management:
   - Create a new lecturer/student
   - Edit user name/role/enabled inline
3. Open course management:
   - Create a course for lecturer
   - Edit course title/description/lecturer inline

### 3.2 Lecturer demo
1. Login as lecturer.
2. Create assignment for owned course.
3. Upload one material.
4. Open submissions list.
5. Grade one submission with score + feedback.

### 3.3 Student demo
1. Login/register as student.
2. Enroll one course.
3. Open submission page.
4. Select assignment from dropdown.
5. Submit with github url + file.
6. View grade/material in student dashboard.

## 4. Role Credentials (Example)
Use sample data account names from DB scripts. Khong hardcode password that len git.

## 5. Smoke Test Checklist
- [ ] Register student works
- [ ] Login + refresh works
- [ ] Admin user/course CRUD works
- [ ] Lecturer assignment create/list works
- [ ] Student assignment list from enrolled courses works
- [ ] Submission upload works (Cloudinary)
- [ ] Grading works and student sees score/feedback
- [ ] Logout token is blacklisted (MySQL)

## 6. Troubleshooting
### 6.1 401 after some requests
- Check access token expiry.
- Verify refresh endpoint response.
- Verify frontend interceptor retries once.

### 6.2 Logout but token still usable
- Verify `token_blacklist` has inserted token.
- Verify JWT filter checks blacklist for each request.

### 6.3 File upload fails
- Verify Cloudinary env vars in backend `.env`.
- Check backend exception log for Cloudinary API error.

### 6.4 CORS issues
- Verify frontend origin is allowed in backend CORS config.
- Use one frontend host during demo (`localhost:5173`).

### 6.5 MySQL connection fail
- Verify DB server running.
- Verify username/password in `.env`.
- Verify target schema exists.

## 7. Performance and Observability
- Execution time logging enabled via AOP for selected methods.
- Use logs to spot slow API paths during demo.

## 8. Security Notes
- Current blacklist strategy: MySQL (`token_blacklist`).
- Redis is intentionally removed in this branch.
- Keep JWT secret and Cloudinary keys outside source code.

## 9. Suggested Production Hardening
1. Introduce strict refresh token rotation and revocation table.
2. Add rate limiting for auth endpoints.
3. Add centralized audit logging for admin actions.
4. Add CI/CD with test + build gates.
5. Move from local env file to secret manager.
