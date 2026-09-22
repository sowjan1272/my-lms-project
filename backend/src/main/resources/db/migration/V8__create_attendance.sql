CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NULL,
    staff_id BIGINT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT','ABSENT','LEAVE','HOLIDAY') NOT NULL,
    check_in TIME NULL,
    check_out TIME NULL,
    remarks VARCHAR(255) NULL,
    marked_by_user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_attendance_staff FOREIGN KEY (staff_id) REFERENCES staff(id),
    CONSTRAINT fk_attendance_marked_by FOREIGN KEY (marked_by_user_id) REFERENCES users(id),
    CONSTRAINT chk_attendance_owner CHECK ((student_id IS NOT NULL AND staff_id IS NULL) OR (student_id IS NULL AND staff_id IS NOT NULL)),
    UNIQUE KEY uq_attendance_student_date (student_id, attendance_date),
    UNIQUE KEY uq_attendance_staff_date (staff_id, attendance_date),
    INDEX idx_attendance_date (attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
