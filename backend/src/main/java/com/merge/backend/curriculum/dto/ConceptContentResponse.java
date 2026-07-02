package com.merge.backend.curriculum.dto;

import com.merge.backend.curriculum.domain.ConceptContent;

import java.time.Instant;

public record ConceptContentResponse(
        Long conceptId,
        String conceptName,
        String failureScenario,
        String mergeExplanation,
        Instant generatedAt
) {
    public static ConceptContentResponse from(ConceptContent content) {
        return new ConceptContentResponse(
                content.getConcept().getId(),
                content.getConcept().getName(),
                content.getConcept().getFailureScenario(),
                content.getExplanationText(),
                content.getGeneratedAt()
        );
    }
}
