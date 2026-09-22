package com.company.studentmanagement.dto.student;

public record UpdateStudentRequest(
        String fullName,
        String phone,
        String address,
        String emergencyContact,
        String qualification,
        String college,
        String photoUrl
) {}
