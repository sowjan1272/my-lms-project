CREATE TABLE salary_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    salary_month CHAR(7) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status ENUM('PENDING','PROCESSING','PAID','FAILED') NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP NULL,
    method VARCHAR(50) NULL,
    transaction_ref VARCHAR(100) NULL,
    remarks VARCHAR(255) NULL,
    CONSTRAINT fk_salary_staff FOREIGN KEY (staff_id) REFERENCES staff(id),
    UNIQUE KEY uq_salary_staff_month (staff_id, salary_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
