package com.merge.backend.curriculum.domain;

import com.merge.backend.identity.domain.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "resource_completions",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_resource_completions_student_resource",
        columnNames = {"student_id", "resource_id"}
    )
)
@Data
@NoArgsConstructor
public class ResourceCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "resource_id", nullable = false)
    private ConceptResource resource;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;
}
