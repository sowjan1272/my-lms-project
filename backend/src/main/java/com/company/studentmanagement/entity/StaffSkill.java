package com.company.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_skills")
@Getter
@Setter
@NoArgsConstructor
public class StaffSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false, length = 100)
    private String skill;

    public StaffSkill(Staff staff, String skill) {
        this.staff = staff;
        this.skill = skill;
    }
}
