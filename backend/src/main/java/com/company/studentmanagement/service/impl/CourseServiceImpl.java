package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.course.CourseDto;
import com.company.studentmanagement.dto.course.CreateCourseRequest;
import com.company.studentmanagement.entity.Course;
import com.company.studentmanagement.entity.CourseModule;
import com.company.studentmanagement.entity.Staff;
import com.company.studentmanagement.enums.CourseType;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.CourseRepository;
import com.company.studentmanagement.repository.EnrollmentRepository;
import com.company.studentmanagement.repository.StaffRepository;
import com.company.studentmanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final StaffRepository staffRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<CourseDto> listAll(String type) {
        List<Course> courses = (type == null || type.isBlank())
                ? courseRepository.findByActiveTrue()
                : courseRepository.findByType(CourseType.valueOf(type));
        return courses.stream().map(this::toDto).toList();
    }

    @Override
    public CourseDto getById(Long id) {
        return toDto(getEntity(id));
    }

    @Override
    @Transactional
    public CourseDto create(CreateCourseRequest request) {
        Course course = new Course();
        applyRequest(course, request);
        course = courseRepository.save(course);
        return toDto(course);
    }

    @Override
    @Transactional
    public CourseDto update(Long id, CreateCourseRequest request) {
        Course course = getEntity(id);
        applyRequest(course, request);
        courseRepository.save(course);
        return toDto(course);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Course course = getEntity(id);
        course.setActive(false);
        courseRepository.save(course);
    }

    @Override
    public List<CourseDto> listForMentor(Long staffId) {
        return courseRepository.findByMentorId(staffId).stream().map(this::toDto).toList();
    }

    private void applyRequest(Course course, CreateCourseRequest request) {
        course.setName(request.name());
        course.setDescription(request.description());
        course.setType(CourseType.valueOf(request.type()));
        course.setDurationMonths(request.durationMonths());
        course.setFee(request.fee());
        if (request.mentorId() != null) {
            Staff mentor = staffRepository.findById(request.mentorId())
                    .orElseThrow(() -> ApiException.notFound("Mentor staff not found"));
            course.setMentor(mentor);
        }
        course.getModules().clear();
        if (request.modules() != null) {
            int order = 0;
            for (String m : request.modules()) {
                course.getModules().add(new CourseModule(course, m, order++));
            }
        }
    }

    private Course getEntity(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> ApiException.notFound("Course not found"));
    }

    private CourseDto toDto(Course c) {
        long enrolled = enrollmentRepository.countByCourseId(c.getId());
        return new CourseDto(
                c.getId(), c.getName(), c.getDescription(), c.getType().name(),
                c.getDurationMonths(), c.getFee(),
                c.getMentor() != null ? c.getMentor().getId() : null,
                c.getMentor() != null ? c.getMentor().getFullName() : null,
                c.isActive(),
                c.getModules().stream().map(CourseModule::getTitle).toList(),
                enrolled
        );
    }
}
