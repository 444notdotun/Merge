package com.merge.backend.curriculum.dto;

/**
 * Data passed to GeminiGateway.generateConceptExplanation.
 * Carries both the concept metadata and the student's personalisation signals
 * so the CurriculumWriter prompt can tailor the 500-800 word explanation.
 */
public record ConceptExplanationRequest(
        String conceptName,
        String sfiaSkill,
        String failureScenario,
        String scaffoldingLevel,
        String thinkingStyle,
        String learningApproach,
        String priorExposure
) {}
