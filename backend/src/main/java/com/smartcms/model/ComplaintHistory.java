package com.smartcms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tracks the history/timeline of a complaint.
 * Stored inside Complaint as a List (Collections).
 */
public class ComplaintHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String performedBy;
    private String action;
    private ComplaintStatus status;
    private LocalDateTime timestamp;

    public ComplaintHistory() {}

    public ComplaintHistory(String performedBy, String action, ComplaintStatus status, LocalDateTime timestamp) {
        this.performedBy = performedBy;
        this.action = action;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
