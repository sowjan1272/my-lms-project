CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT NULL,
    type ENUM('COURSE','INTERNSHIP') NOT NULL,
    duration_months INT NOT NULL,
    fee DECIMAL(12,2) NOT NULL,
    mentor_staff_id BIGINT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_mentor FOREIGN KEY (mentor_staff_id) REFERENCES staff(id),
    INDEX idx_courses_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
