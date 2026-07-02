package com.merge.backend.assessment.dto;

import com.merge.backend.assessment.domain.Build;

import java.time.Instant;
import java.util.List;

public record BuildStatusResponse(
        Long buildId,
        String stageName,
        Instant unlockedAt,
        String prd,
        List<String> requirements,
        List<String> constraints,
        List<String> sfiaCompetencies
) {
    public static BuildStatusResponse from(Build build) {
        return new BuildStatusResponse(
                build.getId(),
                build.getStageName(),
                build.getUnlockedAt(),
                build.getPrd(),
                build.getRequirements(),
                build.getConstraints(),
                build.getSfiaCompetencies()
        );
    }
}
