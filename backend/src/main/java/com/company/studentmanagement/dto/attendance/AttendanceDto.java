package com.company.studentmanagement.dto.attendance;

import java.time.LocalDate;
import java.time.LocalTime;

public record AttendanceDto(
        Long id,
        LocalDate date,
        String status,
        String approvalStatus,
        LocalTime checkIn,
        LocalTime checkOut,
        String remarks
) {}
