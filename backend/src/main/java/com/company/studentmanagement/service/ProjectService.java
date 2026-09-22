package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.project.*;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> listAll();
    List<ProjectDto> listForStaff(Long staffId);
    List<ProjectDto> listForStudent(Long studentId);
    ProjectDto create(CreateProjectRequest request);
    ProjectDto update(Long id, UpdateProjectRequest request);
    ProjectDto getById(Long id);
}
