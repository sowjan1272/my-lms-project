package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.auth.*;
import com.company.studentmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/register/student")
    public ResponseEntity<Void> registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/staff")
    public ResponseEntity<Void> registerStaff(@Valid @RequestBody RegisterStaffRequest request) {
        authService.registerStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admins")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        authService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
