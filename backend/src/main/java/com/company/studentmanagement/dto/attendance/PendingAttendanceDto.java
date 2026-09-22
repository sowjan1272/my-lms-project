package com.company.studentmanagement.dto.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PendingAttendanceDto(
        Long id,
        Long personId,
        String personName,
        LocalDate date,
        String status,
        LocalTime checkIn,
        LocalTime checkOut,
        String remarks,
        LocalDateTime requestedAt
) {}
