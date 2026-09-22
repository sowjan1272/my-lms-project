package com.company.studentmanagement.dto.auth;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RegisterStaffRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String confirmPassword,
        @NotBlank String phone,
        String address,
        @NotNull LocalDate dateOfJoining,
        @NotBlank String designation,
        @NotBlank String department,
        String qualification,
        @NotNull BigDecimal salary,
        List<String> skills,
        String photoUrl
) {}
