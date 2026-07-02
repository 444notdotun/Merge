package com.merge.backend.curriculum.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "concept_unlocks",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_concept_unlocks_student_concept",
        columnNames = {"student_id", "concept_id"}
    )
)
@Data
@NoArgsConstructor
public class ConceptUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt;
}
