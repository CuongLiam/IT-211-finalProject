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

CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(1024) NOT NULL UNIQUE,
    expired_at DATETIME NOT NULL,
    blacklisted_at DATETIME NOT NULL
);