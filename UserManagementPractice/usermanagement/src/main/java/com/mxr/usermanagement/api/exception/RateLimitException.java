package com.mxr.usermanagement.api.exception;

public class RateLimitException extends RuntimeException {
    RateLimitException(String message) {
        super(message);
    }
}
