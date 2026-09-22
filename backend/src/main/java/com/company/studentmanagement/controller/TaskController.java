package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.task.*;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.StaffService;
import com.company.studentmanagement.service.StudentService;
import com.company.studentmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public List<TaskDto> create(@Valid @RequestBody CreateTaskRequest request) {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return taskService.createForStudents(staff.getId(), request);
    }

    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<TaskDto> myTasks() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return taskService.listForStudent(student.getId());
    }

    @GetMapping("/staff/me")
    @PreAuthorize("hasRole('STAFF')")
    public List<TaskDto> myAssignedTasks(@RequestParam(required = false) String status) {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return taskService.listForStaff(staff.getId(), status);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskDto> listAll() {
        return taskService.listAll();
    }

    @GetMapping("/{id}")
    public TaskDto getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping("/{id}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public TaskSubmissionDto submit(@PathVariable Long id, @RequestBody SubmitTaskRequest request) {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return taskService.submit(student.getId(), id, request);
    }

    @GetMapping("/{id}/submissions")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<TaskSubmissionDto> submissions(@PathVariable Long id) {
        return taskService.submissions(id);
    }

    @PatchMapping("/{id}/submissions/{submissionId}")
    @PreAuthorize("hasRole('STAFF')")
    public TaskDto review(@PathVariable Long id, @PathVariable Long submissionId,
                           @Valid @RequestBody ReviewSubmissionRequest request) {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return taskService.review(staff.getId(), id, submissionId, request);
    }
}
