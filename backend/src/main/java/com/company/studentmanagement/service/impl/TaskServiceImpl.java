package com.company.studentmanagement.service.impl;

import com.company.studentmanagement.dto.task.*;
import com.company.studentmanagement.entity.*;
import com.company.studentmanagement.enums.TaskPriority;
import com.company.studentmanagement.enums.TaskStatus;
import com.company.studentmanagement.exception.ApiException;
import com.company.studentmanagement.repository.*;
import com.company.studentmanagement.service.ActivityLogService;
import com.company.studentmanagement.service.NotificationService;
import com.company.studentmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public List<TaskDto> createForStudents(Long staffId, CreateTaskRequest request) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> ApiException.notFound("Staff not found"));

        return request.studentIds().stream().map(studentId -> {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> ApiException.notFound("Student not found: " + studentId));

            Task task = new Task();
            task.setTitle(request.title());
            task.setDescription(request.description());
            task.setCategory(request.category());
            task.setPriority(request.priority() != null ? TaskPriority.valueOf(request.priority()) : TaskPriority.MEDIUM);
            task.setStatus(TaskStatus.PENDING);
            task.setAssignedBy(staff);
            task.setStudent(student);
            task.setAttachmentUrl(request.attachmentUrl());
            task.setDueDate(request.dueDate());
            task = taskRepository.save(task);

            notificationService.notify(student.getUser(), "New task assigned",
                    "\"" + task.getTitle() + "\" assigned by " + staff.getFullName() + ", due " + task.getDueDate());
            activityLogService.log(staff.getUser().getId(), "TASK_CREATED", "TASK", task.getId());

            return toDto(task);
        }).toList();
    }

    @Override
    public List<TaskDto> listForStudent(Long studentId) {
        return taskRepository.findByStudentIdOrderByDueDateAsc(studentId).stream().map(this::toDto).toList();
    }

    @Override
    public List<TaskDto> listForStaff(Long staffId, String status) {
        if (status != null && !status.isBlank()) {
            return taskRepository.findByAssignedByIdAndStatus(staffId, TaskStatus.valueOf(status))
                    .stream().map(this::toDto).toList();
        }
        return taskRepository.findByAssignedById(staffId,
                org.springframework.data.domain.PageRequest.of(0, 500)).getContent()
                .stream().map(this::toDto).toList();
    }

    @Override
    public List<TaskDto> listAll() {
        return taskRepository.findAllByOrderByDueDateAsc().stream().map(this::toDto).toList();
    }

    @Override
    public TaskDto getById(Long taskId) {
        return toDto(getEntity(taskId));
    }

    @Override
    @Transactional
    public TaskSubmissionDto submit(Long studentId, Long taskId, SubmitTaskRequest request) {
        Task task = getEntity(taskId);
        if (!task.getStudent().getId().equals(studentId)) {
            throw ApiException.forbidden("You can only submit your own tasks.");
        }

        TaskSubmission submission = new TaskSubmission();
        submission.setTask(task);
        submission.setStudent(task.getStudent());
        submission.setTextResponse(request.textResponse());
        submission.setFileUrl(request.fileUrl());
        submission.setGithubLink(request.githubLink());
        submission.setDemoLink(request.demoLink());
        submission = taskSubmissionRepository.save(submission);

        task.setStatus(TaskStatus.SUBMITTED);
        taskRepository.save(task);

        notificationService.notify(task.getAssignedBy().getUser(), "Task submitted",
                task.getStudent().getFullName() + " submitted \"" + task.getTitle() + "\"");
        activityLogService.log(task.getStudent().getUser().getId(), "TASK_SUBMITTED", "TASK", task.getId());

        return toSubmissionDto(submission);
    }

    @Override
    public List<TaskSubmissionDto> submissions(Long taskId) {
        return taskSubmissionRepository.findByTaskIdOrderBySubmittedAtDesc(taskId)
                .stream().map(this::toSubmissionDto).toList();
    }

    @Override
    @Transactional
    public TaskDto review(Long staffId, Long taskId, Long submissionId, ReviewSubmissionRequest request) {
        Task task = getEntity(taskId);
        if (!task.getAssignedBy().getId().equals(staffId)) {
            throw ApiException.forbidden("You can only review tasks you assigned.");
        }
        TaskSubmission submission = taskSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> ApiException.notFound("Submission not found"));

        submission.setStaffFeedback(request.feedback());
        submission.setReviewedAt(LocalDateTime.now());
        taskSubmissionRepository.save(submission);

        TaskStatus newStatus = "COMPLETED".equalsIgnoreCase(request.decision())
                ? TaskStatus.COMPLETED : TaskStatus.NEEDS_REVISION;
        task.setStatus(newStatus);
        taskRepository.save(task);

        notificationService.notify(task.getStudent().getUser(), "Task feedback received",
                "\"" + task.getTitle() + "\" marked as " + newStatus.name().replace('_', ' '));
        activityLogService.log(task.getAssignedBy().getUser().getId(), "TASK_REVIEWED", "TASK", task.getId());

        return toDto(task);
    }

    @Override
    @Transactional
    public void markOverdueTasks() {
        LocalDate today = LocalDate.now();
        taskRepository.findAll().stream()
                .filter(t -> t.getDueDate().isBefore(today))
                .filter(t -> t.getStatus() == TaskStatus.PENDING || t.getStatus() == TaskStatus.IN_PROGRESS)
                .forEach(t -> {
                    t.setStatus(TaskStatus.OVERDUE);
                    taskRepository.save(t);
                });
    }

    private Task getEntity(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> ApiException.notFound("Task not found"));
    }

    private TaskDto toDto(Task t) {
        return new TaskDto(t.getId(), t.getTitle(), t.getDescription(), t.getCategory(),
                t.getPriority().name(), t.getStatus().name(),
                t.getAssignedBy().getId(), t.getAssignedBy().getFullName(), t.getAssignedBy().getPhotoUrl(),
                t.getStudent().getId(), t.getStudent().getFullName(),
                t.getDueDate(), t.getAttachmentUrl(), t.getCreatedAt());
    }

    private TaskSubmissionDto toSubmissionDto(TaskSubmission s) {
        return new TaskSubmissionDto(s.getId(), s.getTextResponse(), s.getFileUrl(), s.getGithubLink(),
                s.getDemoLink(), s.getSubmittedAt(), s.getStaffFeedback(), s.getReviewedAt());
    }
}
