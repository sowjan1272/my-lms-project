package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {
    List<TaskSubmission> findByTaskIdOrderBySubmittedAtDesc(Long taskId);
}
