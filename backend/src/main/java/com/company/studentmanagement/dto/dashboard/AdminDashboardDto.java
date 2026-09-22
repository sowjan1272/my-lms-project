package com.company.studentmanagement.dto.dashboard;

import java.math.BigDecimal;

public record AdminDashboardDto(
        long totalStudents,
        long activeStudents,
        long completedStudents,
        long totalStaff,
        long activeStaff,
        long todaysAttendanceMarked,
        long pendingTasks,
        long completedTasks,
        BigDecimal totalRevenue,
        long salaryPendingCount
) {}
