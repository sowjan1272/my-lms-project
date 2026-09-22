CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NULL,
    priority ENUM('LOW','MEDIUM','HIGH','URGENT') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('PENDING','IN_PROGRESS','SUBMITTED','COMPLETED','OVERDUE','NEEDS_REVISION') NOT NULL DEFAULT 'PENDING',
    assigned_by_staff_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attachment_url VARCHAR(500) NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_staff FOREIGN KEY (assigned_by_staff_id) REFERENCES staff(id),
    CONSTRAINT fk_tasks_student FOREIGN KEY (student_id) REFERENCES students(id),
    INDEX idx_tasks_student_status (student_id, status),
    INDEX idx_tasks_staff (assigned_by_staff_id),
    INDEX idx_tasks_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
