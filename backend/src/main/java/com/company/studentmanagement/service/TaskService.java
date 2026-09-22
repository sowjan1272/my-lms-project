package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.task.*;

import java.util.List;

public interface TaskService {
    List<TaskDto> createForStudents(Long staffId, CreateTaskRequest request);
    List<TaskDto> listForStudent(Long studentId);
    List<TaskDto> listForStaff(Long staffId, String status);
    List<TaskDto> listAll();
    TaskDto getById(Long taskId);
    TaskSubmissionDto submit(Long studentId, Long taskId, SubmitTaskRequest request);
    List<TaskSubmissionDto> submissions(Long taskId);
    TaskDto review(Long staffId, Long taskId, Long submissionId, ReviewSubmissionRequest request);
    void markOverdueTasks();
}
