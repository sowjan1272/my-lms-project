package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.student.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.StaffService;
import com.company.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<StudentSummaryDto> listAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return studentService.listAll(search, page, size);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('STAFF')")
    public PageResponse<StudentSummaryDto> myStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return studentService.listForMentor(staff.getId(), page, size);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentDetailDto myProfile() {
        return studentService.getBySelf(currentUser.userId());
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentDetailDto updateMyProfile(@RequestBody UpdateStudentRequest request) {
        return studentService.updateSelf(currentUser.userId(), request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public StudentDetailDto getById(@PathVariable Long id) {
        if ("STAFF".equals(currentUser.role())) {
            var staff = staffService.getEntityByUserId(currentUser.userId());
            var student = studentService.getEntityOrThrow(id);
            if (student.getMentor() == null || !student.getMentor().getId().equals(staff.getId())) {
                throw com.company.studentmanagement.exception.ApiException.forbidden(
                        "You can only view students assigned to you.");
            }
        }
        return studentService.getById(id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDetailDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStudentStatusRequest request) {
        return studentService.updateStatus(id, request.status());
    }

    @PatchMapping("/{id}/mentor")
    @PreAuthorize("hasRole('ADMIN')")
    public StudentDetailDto assignMentor(@PathVariable Long id, @Valid @RequestBody AssignMentorRequest request) {
        return studentService.assignMentor(id, request.staffId());
    }
}
