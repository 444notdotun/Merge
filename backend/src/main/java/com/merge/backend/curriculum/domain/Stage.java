package com.merge.backend.curriculum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stages")
@Data
@NoArgsConstructor
public class Stage {

    /** Stage identifier — SCOUT, CADET, ENGINEER, ARCHITECT, PRINCIPAL. Natural PK. */
    @Id
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    /** Minimum XP earned to become eligible for promotion. 0 = auto on Scout completion. */
    @Column(name = "xp_threshold", nullable = false)
    private int xpThreshold;

    /** Minimum Build pass score required alongside XP for promotion. */
    @Column(name = "build_pass_score_threshold", nullable = false)
    private int buildPassScoreThreshold;

    /** NONE | REVIEWER | PAIR_PROGRAMMER | FULL_ACCELERATOR */
    @Column(name = "ai_access_level", nullable = false, length = 30)
    private String aiAccessLevel;

    /** Syntax micro-exercises available — Cadet only in MVP. */
    @Column(name = "has_syntax_exercises", nullable = false)
    private boolean hasSyntaxExercises;

    /** Peer review available from Engineer stage onwards. */
    @Column(name = "has_peer_review", nullable = false)
    private boolean hasPeerReview;

    /** Clean Code feedback rubric applied: NAMING_ONLY | NAMING_SIZE_REDUNDANCY | FULL_SOLID | HUMAN_REVIEW */
    @Column(name = "clean_code_level", nullable = false, length = 30)
    private String cleanCodeLevel;

    /**
     * Minimum clean-code score (0–100) a Build submission must achieve to pass Gate 3.
     * Evaluated against the AI-07 (CleanCodeReviewer) score for the submission's rubric level.
     * DB default 0 lets existing rows survive ALTER TABLE; seed.sql sets per-stage values.
     */
    @Column(name = "clean_code_min_score", nullable = false,
            columnDefinition = "integer not null default 0")
    private int cleanCodeMinScore;
}
