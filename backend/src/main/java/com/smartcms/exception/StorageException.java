package com.smartcms.exception;

public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super("Storage error: " + message);
    }
    public StorageException(String message, Throwable cause) {
        super("Storage error: " + message, cause);
    }
}
