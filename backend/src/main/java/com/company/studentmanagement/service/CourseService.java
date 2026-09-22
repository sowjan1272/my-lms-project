package com.company.studentmanagement.service;

import com.company.studentmanagement.dto.course.CourseDto;
import com.company.studentmanagement.dto.course.CreateCourseRequest;

import java.util.List;

public interface CourseService {
    List<CourseDto> listAll(String type);
    CourseDto getById(Long id);
    CourseDto create(CreateCourseRequest request);
    CourseDto update(Long id, CreateCourseRequest request);
    void deactivate(Long id);
    List<CourseDto> listForMentor(Long staffId);
}
