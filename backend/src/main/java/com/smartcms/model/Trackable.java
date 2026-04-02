package com.smartcms.model;

import java.time.LocalDateTime;

/**
 * Interface for trackable entities (Complaint).
 * Demonstrates interface design and polymorphism.
 */
public interface Trackable {
    String getComplaintId();
    ComplaintStatus getStatus();
    LocalDateTime getCreatedAt();
    LocalDateTime getLastUpdated();
    String getTrackingSummary();
}
