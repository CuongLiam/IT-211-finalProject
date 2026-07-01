CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    lecturer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_courses_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    enrolled_at DATETIME NOT NULL,
    CONSTRAINT uk_enrollments_course_student UNIQUE (course_id, student_id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    due_date DATETIME NOT NULL,
    max_score DECIMAL(5,2) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_assignments_course FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE IF NOT EXISTS submissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    assignment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    github_url VARCHAR(300) NOT NULL,
    file_url VARCHAR(500),
    original_file_name VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    submitted_at DATETIME NOT NULL,
    CONSTRAINT uk_submissions_assignment_student UNIQUE (assignment_id, student_id),
    CONSTRAINT fk_submissions_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    CONSTRAINT fk_submissions_student FOREIGN KEY (student_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS grades (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    submission_id BIGINT NOT NULL UNIQUE,
    lecturer_id BIGINT NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    feedback TEXT,
    graded_at DATETIME NOT NULL,
    CONSTRAINT fk_grades_submission FOREIGN KEY (submission_id) REFERENCES submissions(id),
    CONSTRAINT fk_grades_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS lecture_materials (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    description TEXT,
    file_url VARCHAR(500) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    uploaded_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_materials_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_materials_lecturer FOREIGN KEY (lecturer_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1024) NOT NULL UNIQUE,
    expired_at DATETIME NOT NULL,
    blacklisted_at DATETIME NOT NULL
);

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE grades;
TRUNCATE TABLE submissions;
TRUNCATE TABLE lecture_materials;
TRUNCATE TABLE assignments;
TRUNCATE TABLE enrollments;
TRUNCATE TABLE courses;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users (id, full_name, email, password, role, enabled, created_at, updated_at)
VALUES
    (1, 'Admin User', 'admin@it211.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Qj3v0L4Qf6fY9fJwRoTUb4fQxN9e1K', 'ADMIN', TRUE, NOW(), NOW()),
    (2, 'Nguyen Van Lecturer', 'lecturer1@it211.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Qj3v0L4Qf6fY9fJwRoTUb4fQxN9e1K', 'LECTURER', TRUE, NOW(), NOW()),
    (3, 'Tran Thi Lecturer', 'lecturer2@it211.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Qj3v0L4Qf6fY9fJwRoTUb4fQxN9e1K', 'LECTURER', TRUE, NOW(), NOW()),
    (4, 'Le Van Student', 'student1@it211.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Qj3v0L4Qf6fY9fJwRoTUb4fQxN9e1K', 'STUDENT', TRUE, NOW(), NOW()),
    (5, 'Pham Thi Student', 'student2@it211.local', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5Qj3v0L4Qf6fY9fJwRoTUb4fQxN9e1K', 'STUDENT', TRUE, NOW(), NOW());

INSERT INTO courses (id, code, name, description, lecturer_id, created_at, updated_at)
VALUES
    (1, 'IT211-JAVA', 'Java Web Service', 'Mon hoc backend voi Spring Boot', 2, NOW(), NOW()),
    (2, 'IT212-DB', 'Database Systems', 'Mon hoc he quan tri co so du lieu', 3, NOW(), NOW());

INSERT INTO enrollments (id, course_id, student_id, status, enrolled_at)
VALUES
    (1, 1, 4, 'ACTIVE', NOW()),
    (2, 1, 5, 'ACTIVE', NOW()),
    (3, 2, 4, 'ACTIVE', NOW());

INSERT INTO assignments (id, course_id, title, description, due_date, max_score, created_at)
VALUES
    (1, 1, 'Assignment 1 - Auth API', 'Xay dung login/register bang JWT', DATE_ADD(NOW(), INTERVAL 7 DAY), 10.00, NOW()),
    (2, 1, 'Assignment 2 - Submission API', 'Nop bai bang github url va file', DATE_ADD(NOW(), INTERVAL 14 DAY), 10.00, NOW()),
    (3, 2, 'Assignment 1 - SQL Practice', 'Viet truy van SQL co join/group by', DATE_ADD(NOW(), INTERVAL 10 DAY), 10.00, NOW());

INSERT INTO submissions (id, assignment_id, student_id, github_url, file_url, original_file_name, status, submitted_at)
VALUES
    (1, 1, 4, 'https://github.com/student1/it211-assignment-1', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/student1-a1.pdf', 'student1-a1.pdf', 'SUBMITTED', NOW()),
    (2, 1, 5, 'https://github.com/student2/it211-assignment-1', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/student2-a1.pdf', 'student2-a1.pdf', 'RESUBMITTED', NOW()),
    (3, 3, 4, 'https://github.com/student1/it212-sql-assignment', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/student1-sql.docx', 'student1-sql.docx', 'SUBMITTED', NOW());

INSERT INTO grades (id, submission_id, lecturer_id, score, feedback, graded_at)
VALUES
    (1, 1, 2, 8.50, 'API chay on, can bo sung validation edge cases.', NOW()),
    (2, 3, 3, 9.00, 'SQL dung logic, trinh bay ro rang.', NOW());

INSERT INTO lecture_materials (id, course_id, lecturer_id, title, description, file_url, original_file_name, uploaded_at, updated_at)
VALUES
    (1, 1, 2, 'JWT Security Slide', 'Tong quan JWT + Spring Security', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/jwt-security.pptx', 'jwt-security.pptx', NOW(), NOW()),
    (2, 1, 2, 'Submission Guide', 'Huong dan nop bai assignment', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/submission-guide.pdf', 'submission-guide.pdf', NOW(), NOW()),
    (3, 2, 3, 'SQL Join Cheatsheet', 'Tong hop cac lenh JOIN co ban', 'https://res.cloudinary.com/demo/raw/upload/v1/course-management/sql-join-cheatsheet.pdf', 'sql-join-cheatsheet.pdf', NOW(), NOW());

ALTER TABLE users AUTO_INCREMENT = 100;
ALTER TABLE courses AUTO_INCREMENT = 100;
ALTER TABLE enrollments AUTO_INCREMENT = 100;
ALTER TABLE assignments AUTO_INCREMENT = 100;
ALTER TABLE submissions AUTO_INCREMENT = 100;
ALTER TABLE grades AUTO_INCREMENT = 100;
ALTER TABLE lecture_materials AUTO_INCREMENT = 100;