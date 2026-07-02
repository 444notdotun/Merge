package com.merge.backend.assessment.domain;

import com.merge.backend.curriculum.domain.Concept;
import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "drills",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_drills_student_concept_number",
        columnNames = {"student_id", "concept_id", "drill_number"}
    )
)
@Data
@NoArgsConstructor
public class Drill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Drills are personalised — each student gets their own AI-generated version. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    /** 1 for Drill 1, 2 for Drill 2. Both must pass comprehension to unlock the next concept. */
    @Column(name = "drill_number", nullable = false)
    private int drillNumber;

    /** AI-generated problem description the student must solve. */
    @Column(name = "problem_statement", nullable = false, columnDefinition = "text")
    private String problemStatement;

    /** Scaffold code with blanks or intentional errors for the student to complete. */
    @Column(name = "starter_code", nullable = false, columnDefinition = "text")
    private String starterCode;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
}
