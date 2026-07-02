package com.merge.backend.assessment.service;

public class ComprehensionTimerExpiredException extends RuntimeException {

    public ComprehensionTimerExpiredException() {
        super("Comprehension check deadline has passed");
    }
}
