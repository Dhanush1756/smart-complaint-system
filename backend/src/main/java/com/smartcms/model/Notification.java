package com.smartcms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Notification model.
 * Queued notifications for users (processed by NotificationDispatchThread).
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String recipientId;
    private String recipientEmail;
    private String title;
    private String message;
    private String complaintId;
    private NotificationType type;
    private boolean read;
    private boolean sent;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public enum NotificationType {
        STATUS_UPDATE, ASSIGNMENT, ESCALATION, RESOLUTION, FEEDBACK_REQUEST, SYSTEM
    }

    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
        this.sent = false;
    }

    public Notification(String id, String recipientId, String recipientEmail,
                        String title, String message, String complaintId, NotificationType type) {
        this();
        this.id = id;
        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.message = message;
        this.complaintId = complaintId;
        this.type = type;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
