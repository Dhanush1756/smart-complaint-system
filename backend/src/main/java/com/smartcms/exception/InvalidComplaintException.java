package com.smartcms.exception;

public class InvalidComplaintException extends RuntimeException {
    public InvalidComplaintException(String reason) {
        super("Invalid complaint: " + reason);
    }
}
