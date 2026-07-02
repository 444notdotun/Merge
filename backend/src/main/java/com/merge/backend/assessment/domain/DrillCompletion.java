package com.merge.backend.assessment.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "drill_completions")
@Data
@NoArgsConstructor
public class DrillCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drill_id", nullable = false)
    private Drill drill;

    /** Judge0 execution passed (code compiled and tests passed). */
    @Column(name = "judge0_passed", nullable = false)
    private boolean judge0Passed;

    /**
     * Timed comprehension MCQ passed after Judge0 success.
     * Concept unlock requires this to be true for BOTH Drill 1 and Drill 2.
     */
    @Column(name = "comprehension_passed", nullable = false)
    private boolean comprehensionPassed;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;
}
