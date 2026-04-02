package com.smartcms.exception;

public class OfficerUnavailableException extends RuntimeException {
    public OfficerUnavailableException(String officerId) {
        super("Officer is unavailable: " + officerId);
    }
}
