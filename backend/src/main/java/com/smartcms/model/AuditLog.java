package com.smartcms.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AuditLog - tracks all actions performed in the system.
 * Demonstrates file persistence and logging concept.
 */
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String performedBy;
    private String userRole;
    private String action;
    private String entityType;
    private String entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(String id, String performedBy, String userRole, String action,
                    String entityType, String entityId, String details) {
        this();
        this.id = id;
        this.performedBy = performedBy;
        this.userRole = userRole;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) -> %s on %s:%s | %s",
                timestamp, performedBy, userRole, action, entityType, entityId, details);
    }
}
