package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.project.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.ProjectService;
import com.company.studentmanagement.service.StaffService;
import com.company.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final StaffService staffService;
    private final StudentService studentService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProjectDto> listAll() {
        return projectService.listAll();
    }

    @GetMapping("/staff/me")
    @PreAuthorize("hasRole('STAFF')")
    public List<ProjectDto> myStaffProjects() {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return projectService.listForStaff(staff.getId());
    }

    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<ProjectDto> myStudentProjects() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return projectService.listForStudent(student.getId());
    }

    @GetMapping("/{id}")
    public ProjectDto getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ProjectDto create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ProjectDto update(@PathVariable Long id, @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }
}
