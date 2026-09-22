package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.project.*;
import com.company.studentmanagement.entity.Project;
import com.company.studentmanagement.entity.ProjectMember;
import com.company.studentmanagement.enums.ProjectStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;

    @Override
    public List<ProjectDto> listAll() {
        return projectRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<ProjectDto> listForStaff(Long staffId) {
        return projectMemberRepository.findByStaffId(staffId).stream()
                .map(pm -> toDto(pm.getProject())).distinct().toList();
    }

    @Override
    public List<ProjectDto> listForStudent(Long studentId) {
        return projectMemberRepository.findByStudentId(studentId).stream()
                .map(pm -> toDto(pm.getProject())).distinct().toList();
    }

    @Override
    @Transactional
    public ProjectDto create(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setTechnologies(request.technologies());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setGithubLink(request.githubLink());
        project.setLiveLink(request.liveLink());
        project.setStatus(ProjectStatus.PLANNING);
        project = projectRepository.save(project);

        if (request.staffIds() != null) {
            for (Long staffId : request.staffIds()) {
                var staff = staffRepository.findById(staffId)
                        .orElseThrow(() -> ApiException.notFound("Staff not found: " + staffId));
                ProjectMember pm = new ProjectMember();
                pm.setProject(project);
                pm.setStaff(staff);
                projectMemberRepository.save(pm);
            }
        }
        if (request.studentIds() != null) {
            for (Long studentId : request.studentIds()) {
                var student = studentRepository.findById(studentId)
                        .orElseThrow(() -> ApiException.notFound("Student not found: " + studentId));
                ProjectMember pm = new ProjectMember();
                pm.setProject(project);
                pm.setStudent(student);
                projectMemberRepository.save(pm);
            }
        }

        return toDto(project);
    }

    @Override
    @Transactional
    public ProjectDto update(Long id, UpdateProjectRequest request) {
        Project project = getEntity(id);
        if (request.name() != null) project.setName(request.name());
        if (request.description() != null) project.setDescription(request.description());
        if (request.technologies() != null) project.setTechnologies(request.technologies());
        if (request.progressPct() != null) project.setProgressPct(request.progressPct());
        if (request.status() != null) project.setStatus(ProjectStatus.valueOf(request.status()));
        if (request.githubLink() != null) project.setGithubLink(request.githubLink());
        if (request.liveLink() != null) project.setLiveLink(request.liveLink());
        projectRepository.save(project);
        return toDto(project);
    }

    @Override
    public ProjectDto getById(Long id) {
        return toDto(getEntity(id));
    }

    private Project getEntity(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> ApiException.notFound("Project not found"));
    }

    private ProjectDto toDto(Project p) {
        List<String> memberNames = projectMemberRepository.findByProjectId(p.getId()).stream()
                .map(pm -> pm.getStaff() != null ? pm.getStaff().getFullName() : pm.getStudent().getFullName())
                .toList();
        return new ProjectDto(p.getId(), p.getName(), p.getDescription(), p.getTechnologies(),
                p.getStartDate(), p.getEndDate(), p.getProgressPct(), p.getStatus().name(),
                p.getGithubLink(), p.getLiveLink(), memberNames);
    }
}
