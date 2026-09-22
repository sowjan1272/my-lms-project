package com.company.studentmanagement.dto.student;

public record StudentSummaryDto(
        Long id,
        String studentCode,
        String fullName,
        String photoUrl,
        String email,
        String phone,
        String status,
        String mentorName,
        Double attendancePct,
        Double taskCompletionPct
) {}
