package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.salary.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.SalaryService;
import com.company.studentmanagement.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SalaryDto create(@Valid @RequestBody CreateSalaryRequest request) {
        return salaryService.create(request);
    }

    @GetMapping("/staff/me")
    @PreAuthorize("hasRole('STAFF')")
    public List<SalaryDto> myHistory() {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return salaryService.history(staff.getId());
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SalaryDto> history(@PathVariable Long staffId) {
        return salaryService.history(staffId);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public SalaryDto updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateSalaryStatusRequest request) {
        return salaryService.updateStatus(id, null, request, true);
    }
}
