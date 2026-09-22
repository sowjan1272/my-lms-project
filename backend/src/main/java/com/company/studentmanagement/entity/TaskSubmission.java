package com.company.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_submissions")
@Getter
@Setter
@NoArgsConstructor
public class TaskSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "text_response", columnDefinition = "TEXT")
    private String textResponse;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "github_link", length = 500)
    private String githubLink;

    @Column(name = "demo_link", length = 500)
    private String demoLink;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;

    @Column(name = "staff_feedback", columnDefinition = "TEXT")
    private String staffFeedback;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
