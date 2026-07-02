package com.merge.backend.curriculum.controller;

import com.merge.backend.curriculum.dto.ConceptContentResponse;
import com.merge.backend.curriculum.service.ConceptContentService;
import com.merge.backend.curriculum.service.ConceptNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/concepts")
public class ConceptContentController {

    private final ConceptContentService contentService;

    public ConceptContentController(ConceptContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * CU-05: GET /api/v1/concepts/{id}/content
     * Returns the personalised written explanation for this concept.
     * Served from concept_content cache if fresh (personalisation_version matches
     * current profile version); otherwise generates via CurriculumWriter prompt,
     * stores, and returns.
     *
     * Response fields:
     *   failureScenario  — real-world failure from concepts table (shown before explanation)
     *   mergeExplanation — AI-generated 500-800 word personalised explanation
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<ConceptContentResponse> getConceptContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                ConceptContentResponse.from(
                        contentService.getOrGenerate(id, userDetails.getUsername())));
    }

    @ExceptionHandler(ConceptNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ConceptNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
