ALTER TABLE attendance
    ADD COLUMN approval_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'APPROVED' AFTER status,
    ADD COLUMN approved_by_user_id BIGINT NULL AFTER marked_by_user_id,
    ADD COLUMN approved_at TIMESTAMP NULL AFTER approved_by_user_id,
    ADD CONSTRAINT fk_attendance_approved_by FOREIGN KEY (approved_by_user_id) REFERENCES users(id);

CREATE INDEX idx_attendance_approval_status ON attendance (approval_status);
