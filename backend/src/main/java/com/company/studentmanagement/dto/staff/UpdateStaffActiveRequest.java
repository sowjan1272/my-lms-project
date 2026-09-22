package com.company.studentmanagement.dto.staff;

import jakarta.validation.constraints.NotNull;

public record UpdateStaffActiveRequest(@NotNull Boolean active) {}
