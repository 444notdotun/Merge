package com.merge.backend.curriculum.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * AI-generated written explanation of a concept, scoped to a single student.
 * Stored separately from the concepts table because the text is unique per
 * student — it is personalised to their scaffolding level, prior exposure,
 * and learning approach at generation time.
 *
 * One row per (student, concept). Stale when personalisation_version < profile.version;
 * the content generation job regenerates and overwrites the row.
 */
@Entity
@Table(
    name = "concept_content",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_concept_content_student_concept",
        columnNames = {"student_id", "concept_id"}
    )
)
@Data
@NoArgsConstructor
public class ConceptContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    /** AI-generated explanation tailored to this student's personalisation profile. */
    @Column(name = "explanation_text", nullable = false, columnDefinition = "text")
    private String explanationText;

    /** Timestamp when this explanation was generated or last regenerated. */
    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    /**
     * Snapshot of personalisation_profiles.version at generation time.
     * When this is less than the current profile version, the content is stale
     * and should be regenerated on next access.
     */
    @Column(name = "personalisation_version", nullable = false)
    private int personalisationVersion;
}
