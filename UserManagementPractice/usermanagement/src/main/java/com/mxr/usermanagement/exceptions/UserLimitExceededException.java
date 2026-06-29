package com.mxr.usermanagement.exceptions;

public class UserLimitExceededException extends RuntimeException {

    public UserLimitExceededException(String message) {
        super(message);
    }
    
}
