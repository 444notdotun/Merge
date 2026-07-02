package com.merge.backend.assessment.dto;

/**
 * AI-04 prompt payload: asks Gemini to generate comprehension questions
 * grounded in the student's specific implementation details.
 */
public record ComprehensionQuestionsRequest(
        String studentCode,
        String testSuite,
        String conceptName,
        String problemStatement
) {}
