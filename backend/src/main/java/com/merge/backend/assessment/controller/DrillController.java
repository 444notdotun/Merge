package com.merge.backend.assessment.controller;

import com.merge.backend.assessment.dto.DrillResponse;
import com.merge.backend.assessment.service.DrillService;
import com.merge.backend.curriculum.service.ConceptLockedException;
import com.merge.backend.curriculum.service.ConceptNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/concepts")
public class DrillController {

    private final DrillService drillService;

    public DrillController(DrillService drillService) {
        this.drillService = drillService;
    }

    /**
     * GET /api/v1/concepts/{id}/drills
     * Returns Drill 1 and Drill 2 for the concept.
     * Generated on first access via AI-02 and cached per student.
     * Drill 2 carries locked=true until Drill 1 comprehension check passes.
     * 403 if concept not yet unlocked; 404 if concept does not exist.
     */
    @GetMapping("/{id}/drills")
    public ResponseEntity<List<DrillResponse>> getDrills(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(drillService.getDrills(id, userDetails.getUsername()));
    }

    @ExceptionHandler(ConceptNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ConceptNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConceptLockedException.class)
    public ResponseEntity<Map<String, String>> handleLocked(ConceptLockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }
}
