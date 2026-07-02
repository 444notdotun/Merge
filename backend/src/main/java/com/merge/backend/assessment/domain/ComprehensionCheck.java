package com.merge.backend.assessment.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "comprehension_checks")
@Data
@NoArgsConstructor
public class ComprehensionCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drill_id", nullable = false)
    private Drill drill;

    /**
     * The specific submission that triggered this check.
     * Each accepted submission generates a new check with fresh questions,
     * ensuring questions differ across attempts.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "drill_submission_id", nullable = false)
    private DrillSubmission drillSubmission;

    /**
     * AI-04-generated questions grounded in this student's specific code —
     * variable names, function choices, architectural decisions.
     * Stored as JSONB; each element is a question string.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "questions", nullable = false, columnDefinition = "jsonb")
    private List<String> questions;

    @Column(name = "triggered_at", nullable = false)
    private Instant triggeredAt;

    /**
     * Hard deadline for the student to answer all questions.
     * Computed as triggeredAt + (numQuestions × 10 seconds).
     */
    @Column(name = "server_deadline", nullable = false)
    private Instant serverDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ComprehensionCheckStatus status = ComprehensionCheckStatus.PENDING;
}
