package com.smartcms.exception;

public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException(String complaintId) {
        super("Complaint not found with ID: " + complaintId);
    }
}
