package com.merge.backend.assessment.dto;

public record ComprehensionSubmitResponse(boolean passed, Integer xpAwarded) {

    public static ComprehensionSubmitResponse failed() {
        return new ComprehensionSubmitResponse(false, null);
    }

    public static ComprehensionSubmitResponse passed(int xpAwarded) {
        return new ComprehensionSubmitResponse(true, xpAwarded);
    }
}
