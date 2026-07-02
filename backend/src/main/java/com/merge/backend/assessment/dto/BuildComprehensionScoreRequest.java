package com.merge.backend.assessment.dto;

import java.util.List;

/**
 * AI-11 request: scores the student's answers against their specific Build artefacts.
 * Answers must reference concrete details of their own code, architecture, and tests.
 * Generic answers that could apply to any implementation are scored as failed.
 */
public record BuildComprehensionScoreRequest(
        String code,
        String architectureDocument,
        String testSuite,
        List<String> questions,
        List<String> answers
) {}
