package com.merge.backend.assessment.dto;

import com.merge.backend.assessment.domain.BuildSubmission;

import java.time.Instant;
import java.util.List;

public record BuildSubmitResponse(
        Long submissionId,
        Long buildId,
        int attemptNumber,
        String overallStatus,
        int pendingXp,
        Integer xpAwarded,
        Instant submittedAt,
        List<BuildGateResultDto> gates,
        /** Non-null when gates 1–3 all passed and the comprehension check was triggered. */
        Long comprehensionCheckId
) {
    public static BuildSubmitResponse from(BuildSubmission submission,
                                           List<BuildGateResultDto> gates,
                                           Long comprehensionCheckId) {
        return new BuildSubmitResponse(
                submission.getId(),
                submission.getBuild().getId(),
                submission.getAttemptNumber(),
                submission.getOverallStatus().name(),
                submission.getPendingXp(),
                submission.getXpAwarded(),
                submission.getSubmittedAt(),
                gates,
                comprehensionCheckId
        );
    }
}
