package com.merge.backend.engagement.service;

import com.merge.backend.engagement.domain.SessionMood;

import java.util.Arrays;
import java.util.List;

public class InvalidMoodException extends RuntimeException {

    private final List<String> validValues;

    public InvalidMoodException(String received) {
        super("Invalid mood: " + received);
        this.validValues = Arrays.stream(SessionMood.values())
                .map(Enum::name)
                .toList();
    }

    public List<String> getValidValues() {
        return validValues;
    }
}
