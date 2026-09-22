package com.company.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_modules")
@Getter
@Setter
@NoArgsConstructor
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public CourseModule(Course course, String title, Integer sortOrder) {
        this.course = course;
        this.title = title;
        this.sortOrder = sortOrder;
    }
}
