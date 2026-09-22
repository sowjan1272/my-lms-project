package com.company.studentmanagement.dto.student;

import jakarta.validation.constraints.NotBlank;

public record UpdateStudentStatusRequest(@NotBlank String status) {}
