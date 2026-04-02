package com.smartcms.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String action) {
        super("Unauthorized to perform: " + action);
    }
}
