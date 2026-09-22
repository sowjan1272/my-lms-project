package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.enrollment.EnrollStudentRequest;
import com.company.studentmanagement.dto.enrollment.EnrollmentDto;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.EnrollmentService;
import com.company.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EnrollmentDto enroll(@Valid @RequestBody EnrollStudentRequest request) {
        return enrollmentService.enroll(currentUser.userId(), request);
    }

    @GetMapping("/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public List<EnrollmentDto> myEnrollments() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return enrollmentService.listByStudent(student.getId());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public List<EnrollmentDto> listByStudent(@PathVariable Long studentId) {
        return enrollmentService.listByStudent(studentId);
    }
}
