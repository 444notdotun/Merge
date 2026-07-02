package com.merge.backend.progression.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "xp_entries")
@Data
@NoArgsConstructor
public class XpEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Stage the student held when this XP was awarded — used for per-stage cap queries. */
    @Column(name = "stage_type", nullable = false, length = 20)
    private String stageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 25)
    private ActivityType activityType;

    /** Actual XP credited (after cap enforcement — never negative). */
    @Column(name = "xp_amount", nullable = false)
    private int xpAmount;

    /** Optional reference to the source entity (resource id, drill id, etc.). */
    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "earned_at", nullable = false)
    private Instant earnedAt;
}
