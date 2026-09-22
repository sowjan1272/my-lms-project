package com.company.studentmanagement.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record SelfMarkAttendanceRequest(
        @NotNull LocalDate date,
        @NotBlank String status, // PRESENT | ABSENT | LEAVE
        LocalTime checkIn,
        LocalTime checkOut,
        String remarks
) {}
