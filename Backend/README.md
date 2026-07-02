# Backend README

## Overview
Backend for IT211 Course Management System.

Stack:
- Java 21
- Spring Boot 4.1
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Cloudinary
- OpenAPI (Swagger)

Base URL (local):
- http://localhost:8080

## Project Layout
- backend/src/main/java/com/example/backend/controller: REST controllers
- backend/src/main/java/com/example/backend/service: business logic
- backend/src/main/java/com/example/backend/repository: JPA repositories
- backend/src/main/java/com/example/backend/entity: domain entities
- backend/src/main/java/com/example/backend/security: JWT and security filter/config
- backend/src/main/resources/application.properties: runtime config
- db/schema.sql: schema bootstrap script
- db/sample-data.sql: sample data

## Prerequisites
- JDK 21+
- MySQL 8+
- Internet access for Gradle dependencies

## Environment Variables
Create file backend/.env (or set environment variables in your shell):

```properties
JWT_SECRET=<base64-secret>
MYSQL_USERNAME=root
MYSQL_PASSWORD=<your-password>
CLOUDINARY_CLOUD_NAME=<cloud-name>
CLOUDINARY_API_KEY=<api-key>
CLOUDINARY_API_SECRET=<api-secret>
```

## Database Setup
Create database:

```sql
CREATE DATABASE IF NOT EXISTS course_management
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

Optional bootstrap:
1. Run db/schema.sql
2. Run db/sample-data.sql

Note:
- Current JPA mode is update (`spring.jpa.hibernate.ddl-auto=update`).

## Run Locally
From project root:

```bash
cd Backend/backend
gradlew.bat bootRun
```

Expected:
- API available at http://localhost:8080
- Swagger UI available at http://localhost:8080/swagger-ui.html
- OpenAPI docs at http://localhost:8080/api-docs

## Build and Test

```bash
cd Backend/backend
gradlew.bat clean classes
gradlew.bat test
```

Test report:
- backend/build/reports/tests/test/index.html

## Authentication and Security
- Login returns access token + refresh token.
- Protected endpoints require Bearer token.
- Refresh endpoint issues new access token.
- Logout writes token to blacklist table (MySQL based blacklist in current branch).
- Token blacklist is checked in auth filter to reject revoked tokens.

## API Map
All paths are prefixed by /api/v1.

### Auth
- POST /auth/register
- POST /auth/login
- POST /auth/refresh-token
- POST /auth/logout
- POST /auth/forgot-password
- POST /auth/reset-password

### User self-service
- GET /users/me
- PUT /users/me
- POST /users/me/change-password

### Admin
- GET /admin/users
- POST /admin/users
- GET /admin/users/{id}
- PUT /admin/users/{id}
- DELETE /admin/users/{id}
- GET /admin/courses
- POST /admin/courses
- GET /admin/courses/{id}
- PUT /admin/courses/{id}
- DELETE /admin/courses/{id}

### Lecturer
- POST /lecturer/assignments
- GET /lecturer/assignments
- GET /lecturer/submissions
- POST /lecturer/submissions/{submissionId}/grade
- GET /lecturer/grades
- POST /lecturer/materials (multipart)
- GET /lecturer/materials
- PUT /lecturer/materials/{materialId}
- DELETE /lecturer/materials/{materialId}

### Student
- GET /student/courses
- GET /student/courses/enrollments
- GET /student/courses/assignments
- POST /student/courses/{courseId}/enroll
- DELETE /student/courses/{courseId}/enroll
- POST /student/submissions (multipart)
- GET /student/submissions
- GET /student/grades
- GET /student/materials

## Upload Limits
Current multipart settings in application.properties:
- max file size: 15MB
- max request size: 15MB

## Health and Observability
Actuator endpoints exposed:
- /actuator/health
- /actuator/info

Execution-time logging is implemented through AOP for selected layers.

## Common Troubleshooting
1. App fails to connect MySQL:
- Check MySQL service is running.
- Check MYSQL_USERNAME and MYSQL_PASSWORD.
- Check database course_management exists.

2. 401 on protected API:
- Verify Authorization header format: Bearer <token>.
- Verify access token expiry.
- Try refresh-token flow.

3. Logout but token still accepted:
- Verify token_blacklist records are created.
- Verify auth filter checks blacklist on each request.

4. Upload fails:
- Check Cloudinary variables.
- Check file size <= 15MB.
- Check backend logs for Cloudinary API error.

## Notes
- Redis is intentionally not used in this branch for blacklist storage.
- If you switch storage strategy later, update both auth service and security filter validation flow.
