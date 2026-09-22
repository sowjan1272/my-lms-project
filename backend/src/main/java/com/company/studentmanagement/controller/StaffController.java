package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.common.PageResponse;
import com.company.studentmanagement.dto.staff.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<StaffSummaryDto> listAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return staffService.listAll(search, page, size);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STAFF')")
    public StaffDetailDto myProfile() {
        return staffService.getBySelf(currentUser.userId());
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('STAFF')")
    public StaffDetailDto updateMyProfile(@RequestBody UpdateStaffRequest request) {
        return staffService.updateSelf(currentUser.userId(), request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffDetailDto getById(@PathVariable Long id) {
        return staffService.getById(id);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffDetailDto setActive(@PathVariable Long id, @Valid @RequestBody UpdateStaffActiveRequest request) {
        return staffService.setActive(id, request.active());
    }
}
