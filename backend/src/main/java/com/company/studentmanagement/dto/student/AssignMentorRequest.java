package com.company.studentmanagement.dto.student;

import jakarta.validation.constraints.NotNull;

public record AssignMentorRequest(@NotNull Long staffId) {}
