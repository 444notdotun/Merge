package com.mxr.usermanagement.exceptions;

public class IllegalArgumentException extends RuntimeException {
    IllegalArgumentException(String message){
        super(message);
    }
}
