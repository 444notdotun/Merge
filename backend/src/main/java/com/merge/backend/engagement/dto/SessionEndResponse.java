package com.merge.backend.engagement.dto;

public record SessionEndResponse(
        String sessionId,
        long durationMs,
        int drillsCompleted,
        int xpEarned
) {}
