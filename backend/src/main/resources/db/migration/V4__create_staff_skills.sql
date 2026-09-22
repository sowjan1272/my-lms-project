CREATE TABLE staff_skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    skill VARCHAR(100) NOT NULL,
    CONSTRAINT fk_staff_skills_staff FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    INDEX idx_staff_skills_staff (staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
