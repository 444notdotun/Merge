package com.merge.backend.feedback.domain;

import com.merge.backend.assessment.domain.DrillSubmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "clean_code_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanCodeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "drill_submission_id", nullable = false, unique = true)
    private DrillSubmission drillSubmission;

    @Column(name = "overall_score")
    private Integer overallScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "naming_issues", columnDefinition = "jsonb")
    private List<String> namingIssues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "function_size_issues", columnDefinition = "jsonb")
    private List<String> functionSizeIssues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "redundancy_issues", columnDefinition = "jsonb")
    private List<String> redundancyIssues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "solid_issues", columnDefinition = "jsonb")
    private List<String> solidIssues;

    @Column(name = "senior_review_flagged")
    private boolean seniorReviewFlagged = false;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt = Instant.now();
}
