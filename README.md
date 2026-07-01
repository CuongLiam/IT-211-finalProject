# Course Management & Project Grading System

This repository contains a full-stack course management and project grading system built with Spring Boot and React TypeScript.

## 1. Architecture

- Backend: Spring Boot (stateless REST API)
- Frontend: React + TypeScript + Vite + Tailwind
- Database: MySQL
- Authentication: Spring Security + JWT access/refresh token + token blacklist
- File Storage: Cloudinary
- API Docs: Swagger / OpenAPI

Workspace structure:

- Backend/: Spring Boot application source
- Frontend/: React application source
- docs/: optional design and report documents

## 2. Implemented Features

### Sprint 1

- Authentication: register, login, logout, refresh token
- User profile: get/update profile, change password, forgot/reset password
- Admin CRUD users/courses with pagination/search
- Global exception handler and standardized API response format
- Frontend authentication flow, protected routes, role guards

### Sprint 2

- Student course enrollment APIs and frontend page
- Cloudinary setup + file validator
- Student submission API: GitHub link + file upload
- Student submission history frontend
- AOP logging for grading use case
- Lecturer grading API + grade summary frontend

### Sprint 3

- Lecture materials API (upload/list/update/delete)
- Lecturer materials frontend and student materials frontend
- Swagger annotations added on key new endpoints
- Unit test scaffolding recommendation included below

## 3. Backend Setup

Path:

- Backend/backend

Requirements:

- JDK 21
- MySQL 8+

Create database:

```sql
CREATE DATABASE IF NOT EXISTS course_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Set environment variables in IDE run configuration:

- MYSQL_USERNAME
- MYSQL_PASSWORD
- JWT_SECRET (Base64 256-bit secret)
- CLOUDINARY_CLOUD_NAME
- CLOUDINARY_API_KEY
- CLOUDINARY_API_SECRET

Run backend:

```bash
cd Backend/backend
./gradlew bootRun
```

Swagger:

- http://localhost:8080/swagger-ui.html

Health check:

- http://localhost:8080/actuator/health

## 4. Frontend Setup

Path:

- Frontend

Install dependencies:

```bash
cd Frontend
npm install
```

Run dev server:

```bash
npm run dev
```

Default frontend URL:

- http://localhost:5173

## 5. Important API Groups

Authentication:

- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh-token
- POST /api/v1/auth/logout

Student:

- GET /api/v1/student/courses
- POST /api/v1/student/courses/{courseId}/enroll
- DELETE /api/v1/student/courses/{courseId}/enroll
- POST /api/v1/student/submissions (multipart)
- GET /api/v1/student/submissions
- GET /api/v1/student/grades
- GET /api/v1/student/materials

Lecturer:

- GET /api/v1/lecturer/submissions
- POST /api/v1/lecturer/submissions/{submissionId}/grade
- GET /api/v1/lecturer/grades
- POST /api/v1/lecturer/materials (multipart)
- GET /api/v1/lecturer/materials
- PUT /api/v1/lecturer/materials/{materialId}
- DELETE /api/v1/lecturer/materials/{materialId}

Admin:

- CRUD users and courses under /api/v1/admin/**

## 6. Unit Test Plan (Step 23)

Recommended service tests:

- AuthServiceTest
  - login success/failure
  - refresh token validation
  - logout token blacklist behavior
- GradeServiceTest
  - grade submission success
  - forbidden grading for non-owner lecturer
- SubmissionServiceTest
  - upload validation
  - enrollment check
  - first submit and resubmit flow

Use Mockito + Spring Boot Test as currently configured in Gradle.

## 7. Deployment Notes

Backend options:

- Render/Railway with environment variables configured

Frontend options:

- Vercel/Netlify with API base URL pointing to deployed backend

## 8. Known Notes

- The project currently uses `tools.jackson.databind.ObjectMapper` import as configured in the existing codebase.
- Ensure Cloudinary environment variables are present before testing submission/material upload endpoints.
- Some forms currently accept IDs (assignmentId/courseId) directly for faster demo and grading workflow.

## 9. Suggested Next Improvements

- Replace manual ID inputs with dropdowns loaded from APIs
- Add lecturer assignment management endpoints (create/list assignments)
- Add stronger integration tests and role-based E2E flows
- Add CI pipeline (build, test, lint, deploy)
