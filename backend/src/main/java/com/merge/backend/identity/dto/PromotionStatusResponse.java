package com.merge.backend.identity.dto;

/**
 * eligible       — true when both XP and build-score gates are cleared simultaneously.
 * missingXp      — XP still needed to reach stage.xp_threshold; 0 when gate is clear.
 * missingBuildScore — cumulative build overallScore still needed; 0 when gate is clear.
 */
public record PromotionStatusResponse(boolean eligible, int missingXp, int missingBuildScore) {}
