package com.company.studentmanagement.dto.student;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentDetailDto(
        Long id,
        String studentCode,
        String fullName,
        String photoUrl,
        String email,
        LocalDate dob,
        String gender,
        String phone,
        String address,
        String emergencyContact,
        String qualification,
        String college,
        String status,
        Long mentorId,
        String mentorName,
        LocalDateTime createdAt
) {}
