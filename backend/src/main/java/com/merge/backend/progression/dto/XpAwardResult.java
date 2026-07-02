package com.merge.backend.progression.dto;

/**
 * Result of a ProgressionService.awardXp call.
 *
 * awarded — actual XP credited to the student after cap enforcement (0 if already at cap)
 * capped  — true if the cap limited the award (awarded < requested amount)
 */
public record XpAwardResult(int awarded, boolean capped) {

    public static XpAwardResult fullyAwarded(int amount) {
        return new XpAwardResult(amount, false);
    }

    public static XpAwardResult atCap() {
        return new XpAwardResult(0, true);
    }
}
