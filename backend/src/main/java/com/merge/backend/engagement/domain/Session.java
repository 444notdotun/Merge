package com.merge.backend.engagement.domain;

import com.merge.backend.curriculum.domain.Concept;
import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
public class Session {

    /** UUID assigned at creation — referenced throughout the session lifecycle. */
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** The concept the student is working on when this session starts. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood", nullable = false, length = 15)
    private SessionMood mood;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 10)
    private SessionType sessionType;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    /** Set by POST /sessions/{id}/end — null while session is in progress. */
    @Column(name = "ended_at")
    private Instant endedAt;

    /** Count of drills where comprehension passed during this session. Null while in progress. */
    @Column(name = "drills_completed")
    private Integer drillsCompleted;

    /** Total XP awarded to the student during this session. Null while in progress. */
    @Column(name = "xp_earned")
    private Integer xpEarned;
}
