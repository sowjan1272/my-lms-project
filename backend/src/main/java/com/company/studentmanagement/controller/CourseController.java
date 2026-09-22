package com.company.studentmanagement.controller;

import com.company.studentmanagement.dto.course.*;
import com.company.studentmanagement.security.CurrentUserProvider;
import com.company.studentmanagement.service.CourseService;
import com.company.studentmanagement.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final StaffService staffService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    public List<CourseDto> listAll(@RequestParam(required = false) String type) {
        return courseService.listAll(type);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('STAFF')")
    public List<CourseDto> myCourses() {
        var staff = staffService.getEntityByUserId(currentUser.userId());
        return courseService.listForMentor(staff.getId());
    }

    @GetMapping("/{id}")
    public CourseDto getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CourseDto create(@Valid @RequestBody CreateCourseRequest request) {
        return courseService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CourseDto update(@PathVariable Long id, @Valid @RequestBody CreateCourseRequest request) {
        return courseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable Long id) {
        courseService.deactivate(id);
    }
}
