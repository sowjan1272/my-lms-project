CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    enrollment_id BIGINT NULL,
    amount DECIMAL(12,2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    transaction_ref VARCHAR(100) NULL,
    paid_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by_user_id BIGINT NOT NULL,
    CONSTRAINT fk_payments_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_payments_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id),
    CONSTRAINT fk_payments_recorded_by FOREIGN KEY (recorded_by_user_id) REFERENCES users(id),
    INDEX idx_payments_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
