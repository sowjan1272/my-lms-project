package com.company.studentmanagement.dto.auth;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterStudentRequest(
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String confirmPassword,
        @NotNull LocalDate dob,
        String gender,
        @NotBlank String phone,
        String address,
        String emergencyContact,
        String qualification,
        String college,
        String photoUrl
) {}
