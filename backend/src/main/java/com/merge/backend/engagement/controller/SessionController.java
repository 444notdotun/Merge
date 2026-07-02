package com.merge.backend.engagement.controller;

import com.merge.backend.engagement.dto.SessionStartRequest;
import com.merge.backend.engagement.dto.SessionStartResponse;
import com.merge.backend.engagement.service.InvalidMoodException;
import com.merge.backend.engagement.service.SessionStartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionStartService sessionStartService;

    public SessionController(SessionStartService sessionStartService) {
        this.sessionStartService = sessionStartService;
    }

    /**
     * POST /api/v1/sessions/start
     * Creates a new study session for the authenticated student.
     *
     * Body: { "mood": "FRESH" | "OKAY" | "EXHAUSTED" }
     * 201 — { sessionId, conceptId, sessionType }
     * 400 — { validValues } when mood is not a recognised SessionMood value
     * 422 — no concept unlocked for the student's current stage
     */
    @PostMapping("/start")
    public ResponseEntity<SessionStartResponse> start(
            @RequestBody SessionStartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SessionStartResponse response = sessionStartService.start(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(InvalidMoodException.class)
    public ResponseEntity<Map<String, List<String>>> handleInvalidMood(InvalidMoodException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("validValues", ex.getValidValues()));
    }
}
