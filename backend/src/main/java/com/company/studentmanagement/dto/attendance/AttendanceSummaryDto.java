package com.company.studentmanagement.dto.attendance;

public record AttendanceSummaryDto(
        long totalWorkingDays,
        long presentDays,
        long absentDays,
        long leaveDays,
        double attendancePct
) {}
