package com.merge.backend.curriculum.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "concepts",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_concepts_stage_sequence",
        columnNames = {"stage_name", "sequence_order"}
    )
)
@Data
@NoArgsConstructor
public class Concept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stage this concept belongs to — references stages.name. */
    @Column(name = "stage_name", nullable = false, length = 20)
    private String stageName;

    @Column(name = "name", nullable = false)
    private String name;

    /** 1-based position within the stage. Concepts unlock in this order. */
    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    /**
     * SFIA competency this concept primarily develops.
     * One of: PROBLEM_SOLVING, SOFTWARE_DESIGN, CODE_QUALITY, TESTING_AND_DEBUGGING,
     *         SYSTEMS_THINKING, COLLABORATION, ENGINEERING_OWNERSHIP, PROFESSIONAL_GROWTH
     */
    @Column(name = "sfia_skill", nullable = false, length = 30)
    private String sfiaSkill;

    /** Real-world failure scenario shown to David before the concept is taught (FACT framework). */
    @Column(name = "failure_scenario", nullable = false, columnDefinition = "text")
    private String failureScenario;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
