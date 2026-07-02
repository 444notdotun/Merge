package com.merge.backend.engagement.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
    name = "weekly_momentum",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_weekly_momentum_student_week",
        columnNames = {"student_id", "week_start"}
    )
)
@Data
@NoArgsConstructor
public class WeeklyMomentum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** The Monday that starts the 7-day window this record covers. */
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 15)
    private MomentumState state;

    @Column(name = "session_count", nullable = false)
    private int sessionCount;

    /**
     * Drill pass rate for the week: passed / attempted.
     * Null when sessionCount = 0 or no drills were attempted.
     */
    @Column(name = "drill_pass_rate")
    private Double drillPassRate;

    @Column(name = "calculated_at", nullable = false)
    private Instant calculatedAt;
}
