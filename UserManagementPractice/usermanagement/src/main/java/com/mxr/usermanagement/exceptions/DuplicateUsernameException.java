package com.mxr.usermanagement.exceptions;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("Username already exists: " + username);
    }
}
