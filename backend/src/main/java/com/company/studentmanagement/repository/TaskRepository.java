package com.company.studentmanagement.repository;

import com.company.studentmanagement.entity.Task;
import com.company.studentmanagement.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStudentIdOrderByDueDateAsc(Long studentId);
    Page<Task> findByAssignedById(Long staffId, Pageable pageable);
    List<Task> findByAssignedByIdAndStatus(Long staffId, TaskStatus status);
    long countByStudentIdAndStatus(Long studentId, TaskStatus status);
    long countByAssignedByIdAndStatus(Long staffId, TaskStatus status);
    long countByStatus(TaskStatus status);
    List<Task> findAllByOrderByDueDateAsc();
}
