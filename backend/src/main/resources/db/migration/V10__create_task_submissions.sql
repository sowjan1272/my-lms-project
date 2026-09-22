CREATE TABLE task_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    text_response TEXT NULL,
    file_url VARCHAR(500) NULL,
    github_link VARCHAR(500) NULL,
    demo_link VARCHAR(500) NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    staff_feedback TEXT NULL,
    reviewed_at TIMESTAMP NULL,
    CONSTRAINT fk_submissions_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_submissions_student FOREIGN KEY (student_id) REFERENCES students(id),
    INDEX idx_task_submissions_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
