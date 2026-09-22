package com.company.studentmanagement.dto.dashboard;

public record StaffDashboardDto(
        double myAttendancePct,
        long studentsAssigned,
        long activeTasks,
        long completedTasks,
        long pendingTasks,
        long currentProjects,
        String salaryStatusThisMonth
) {}
