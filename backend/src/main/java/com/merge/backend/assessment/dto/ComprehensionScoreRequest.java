package com.merge.backend.assessment.dto;

import java.util.List;

/**
 * AI-05 scoring payload: sends the student's original code alongside the generated
 * questions and their submitted answers so the model can verify that the answers
 * demonstrate genuine understanding of this specific implementation.
 */
public record ComprehensionScoreRequest(
        String studentCode,
        List<String> questions,
        List<String> answers
) {}
