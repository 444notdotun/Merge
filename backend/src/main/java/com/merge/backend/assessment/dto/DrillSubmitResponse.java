package com.merge.backend.assessment.dto;

import com.merge.backend.assessment.domain.DrillSubmission;

import java.time.Instant;

public record DrillSubmitResponse(
        Long submissionId,
        Long drillId,
        String status,
        Boolean testsPassed,
        String stderr,
        Instant submittedAt
) {
    /** Used for idempotency 409 responses — testsPassed derived from stored status. */
    public static DrillSubmitResponse from(DrillSubmission s) {
        boolean passed = switch (s.getStatus()) {
            case JUDGE0_PASS -> true;
            case JUDGE0_FAIL -> false;
            default -> false;
        };
        return new DrillSubmitResponse(
                s.getId(),
                s.getDrill().getId(),
                s.getStatus().name(),
                passed,
                s.getStderr(),
                s.getSubmittedAt()
        );
    }

    public static DrillSubmitResponse from(DrillSubmission s, boolean testsPassed) {
        return new DrillSubmitResponse(
                s.getId(),
                s.getDrill().getId(),
                s.getStatus().name(),
                testsPassed,
                s.getStderr(),
                s.getSubmittedAt()
        );
    }
}
