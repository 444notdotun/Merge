package com.merge.backend.assessment.dto;

/**
 * Returned with HTTP 202 while the BUILD_PRD_GENERATION job is still running.
 * Client should poll GET /api/v1/builds/current until the response becomes 200.
 */
public record BuildGeneratingResponse(boolean generating) {

    public static BuildGeneratingResponse instance() {
        return new BuildGeneratingResponse(true);
    }
}
