CREATE TABLE project_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    staff_id BIGINT NULL,
    student_id BIGINT NULL,
    role_label VARCHAR(100) NULL,
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_staff FOREIGN KEY (staff_id) REFERENCES staff(id),
    CONSTRAINT fk_pm_student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT chk_pm_owner CHECK ((staff_id IS NOT NULL AND student_id IS NULL) OR (staff_id IS NULL AND student_id IS NOT NULL)),
    INDEX idx_project_members_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
