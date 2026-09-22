package com.company.studentmanagement.dto.dashboard;

public record StudentDashboardDto(
        double attendancePct,
        long totalTasks,
        long completedTasks,
        long pendingTasks,
        String currentCourseName,
        java.math.BigDecimal feePaid,
        java.math.BigDecimal feeRemaining
) {}
