# Frontend Guide - IT211 Course Management

## 1. Scope

Frontend la SPA cho he thong quan ly khoa hoc voi 3 vai tro:
- Admin
- Lecturer
- Student

Tech stack:
- React + TypeScript
- Vite
- React Router
- Axios + interceptor (JWT refresh)

## 2. Setup and Run

```bash
cd Frontend
npm install
npm run dev
```

Default URL: http://localhost:5173

Production build:

```bash
npm run build
```

## 3. Folder Layout

- src/api: API clients
- src/context: Auth context
- src/routes: route guards and router config
- src/pages/admin: admin screens
- src/pages/lecturer: lecturer screens
- src/pages/student: student screens
- src/types: shared TS models

## 4. Authentication Flow

1. Login/Register via AuthContext.
2. Tokens saved in localStorage via tokenStorage.
3. axios request interceptor adds Authorization header.
4. On 401, axios response interceptor calls refresh-token.
5. If refresh fails, user is logged out and redirected.

## 5. Role-based Routing

- /admin/users -> ADMIN only
- /lecturer/dashboard -> LECTURER only
- /student/dashboard -> STUDENT only

ProtectedRoute checks auth and role before rendering route.

## 6. UI Flows by Role

### 6.1 Admin

Users tab:
- Search + paging
- Create user
- Edit inline: full name, role, enabled
- Delete user

Courses tab:
- Search + paging
- Create course
- Edit inline: code, name, description, lecturer
- Delete course

### 6.2 Lecturer

- Assignments section:
  - Create assignment
  - List assignments
- Pending submissions section:
  - View submissions
  - Grade + feedback
- Grade summary section
- Lecture materials section:
  - Upload material
  - List materials

### 6.3 Student

- Course enrollment section:
  - Enroll/cancel
  - View enrollments
- Assignment submission section:
  - Select assignment from dropdown (based on enrolled courses)
  - Enter GitHub URL + file upload
  - View submission history
- Lecture materials section
- Grades section (from backend student grade endpoint)

## 7. API Clients

- authApi: login/register/logout
- adminApi: users/courses CRUD
- lecturerApi: assignments, grading, materials
- studentApi: courses, enrollments, assignments list
- submissionApi: submit and submission history
- learningApi: student grades/materials

## 8. Important Notes

1. Frontend expects backend base URL at http://localhost:8080/api/v1.
2. Ensure backend is running before opening protected pages.
3. For submission/material upload, backend Cloudinary credentials must be valid.

## 9. Troubleshooting

1. Stuck on 401 loop
- Check refresh-token endpoint
- Check stored refresh token is not revoked

2. Empty lecturer submissions
- Student must submit assignment first

3. Empty assignment dropdown for student
- Student must enroll a course
- Lecturer must create assignment for that course
