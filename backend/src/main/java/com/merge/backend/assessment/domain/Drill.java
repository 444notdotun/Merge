package com.merge.backend.assessment.domain;

import com.merge.backend.curriculum.domain.Concept;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "drills",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_drills_concept_number",
        columnNames = {"concept_id", "drill_number"}
    )
)
@Data
@NoArgsConstructor
public class Drill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concept_id", nullable = false)
    private Concept concept;

    /** 1 for Drill 1, 2 for Drill 2. Both must pass comprehension to unlock the next concept. */
    @Column(name = "drill_number", nullable = false)
    private int drillNumber;
}
