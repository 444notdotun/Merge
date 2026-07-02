package com.merge.backend.assessment.domain;

/**
 * XP pass tiers for a Build submission.
 *
 * Tiers are determined by which gates passed:
 *   MINIMUM    — gates 1 + 2 pass (gate 3 or comprehension failed)       — 150 XP base
 *   STANDARD   — gates 1 + 2 + 3 + 4 pass (SFIA competency gate failed)  — 300 XP base
 *   DISTINCTION — all 5 gates pass                                         — 500 XP base
 *
 * The actual XP awarded = baseXp × attempt decay (100% / 75% / 50% / 25%).
 */
public enum BuildPassTier {

    MINIMUM(150),
    STANDARD(300),
    DISTINCTION(500);

    public final int baseXp;

    BuildPassTier(int baseXp) {
        this.baseXp = baseXp;
    }

    /** Applies attempt-based decay to the tier's base XP and returns the awarded amount. */
    public int computeXp(int attemptNumber) {
        return switch (attemptNumber) {
            case 1 -> baseXp;
            case 2 -> (int) (baseXp * 0.75);
            case 3 -> (int) (baseXp * 0.50);
            default -> (int) (baseXp * 0.25);
        };
    }
}
