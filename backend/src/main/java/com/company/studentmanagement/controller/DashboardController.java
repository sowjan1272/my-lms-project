package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.dashboard.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.DashboardService;
import com.company.studentmanagement.service.StaffService;
import com.company.studentmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final StudentService studentService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public StudentDashboardDto student() {
        var student = studentService.getEntityByUserId(currentUser.userId());
        return dashboardService.studentDashboard(student.getId());
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF')")
    public StaffDashboardDto staff() {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return dashboardService.staffDashboard(staff.getId());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboardDto admin() {
        return dashboardService.adminDashboard();
    }
}
