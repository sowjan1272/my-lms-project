CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    staff_code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    photo_url VARCHAR(500) NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(500) NULL,
    date_of_joining DATE NOT NULL,
    designation VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    qualification VARCHAR(255) NULL,
    salary_base DECIMAL(12,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_staff_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE students
    ADD CONSTRAINT fk_students_mentor FOREIGN KEY (mentor_staff_id) REFERENCES staff(id);
