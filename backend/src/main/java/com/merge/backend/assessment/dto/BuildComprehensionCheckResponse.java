package com.merge.backend.assessment.dto;

import com.merge.backend.assessment.domain.BuildComprehensionCheck;

import java.time.Instant;
import java.util.List;

public record BuildComprehensionCheckResponse(
        Long id,
        Long buildSubmissionId,
        List<String> questions,
        Instant triggeredAt,
        Instant serverDeadline,
        String status
) {
    public static BuildComprehensionCheckResponse from(BuildComprehensionCheck check) {
        return new BuildComprehensionCheckResponse(
                check.getId(),
                check.getBuildSubmission().getId(),
                check.getQuestions(),
                check.getTriggeredAt(),
                check.getServerDeadline(),
                check.getStatus().name()
        );
    }
}
