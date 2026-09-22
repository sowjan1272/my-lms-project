package com.company.studentmanagement.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record MarkAttendanceRequest(
        Long studentId,
        Long staffId,
        @NotNull LocalDate date,
        @NotBlank String status, // PRESENT | ABSENT | LEAVE | HOLIDAY
        LocalTime checkIn,
        LocalTime checkOut,
        String remarks
) {}
