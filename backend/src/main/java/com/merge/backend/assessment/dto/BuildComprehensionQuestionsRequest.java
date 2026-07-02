package com.merge.backend.assessment.dto;

/**
 * AI-10 request: generates comprehension questions grounded in the student's specific
 * Build artefacts — their actual code decisions, architecture choices, and test strategies.
 */
public record BuildComprehensionQuestionsRequest(
        String code,
        String architectureDocument,
        String testSuite,
        String stageName
) {}
