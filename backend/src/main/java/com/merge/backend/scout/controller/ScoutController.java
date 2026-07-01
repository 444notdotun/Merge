package com.merge.backend.scout.controller;

import com.merge.backend.scout.dto.Layer1QuestionsResponse;
import com.merge.backend.scout.dto.Layer1SubmitRequest;
import com.merge.backend.scout.dto.Layer1SubmitResponse;
import com.merge.backend.scout.service.AlreadySubmittedException;
import com.merge.backend.scout.service.ScoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/scout")
public class ScoutController {

    private final ScoutService scoutService;

    public ScoutController(ScoutService scoutService) {
        this.scoutService = scoutService;
    }

    /**
     * SC-01: GET /api/v1/scout/layer-1
     * Returns the 8 plain-language background intake questions.
     * Requires valid JWT (depends on ID-02).
     */
    @GetMapping("/layer-1")
    public ResponseEntity<Layer1QuestionsResponse> getLayer1Questions() {
        return ResponseEntity.ok(scoutService.getLayer1Questions());
    }

    /**
     * SC-02: POST /api/v1/scout/layer-1/submit
     * Accepts free-text answers and persists them to scout_assessments.layer1_responses (JSONB).
     * Requires valid JWT. One submission per student.
     */
    @PostMapping("/layer-1/submit")
    public ResponseEntity<Layer1SubmitResponse> submitLayer1(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody Layer1SubmitRequest request) {
        Layer1SubmitResponse response = scoutService.submitLayer1(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(AlreadySubmittedException.class)
    public ResponseEntity<?> handleAlreadySubmitted(AlreadySubmittedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
