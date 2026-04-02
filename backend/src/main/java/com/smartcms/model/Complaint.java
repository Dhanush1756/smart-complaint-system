package com.smartcms.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Complaint class implementing Trackable and Resolvable interfaces.
 * Demonstrates:
 * - Interface implementation (Trackable, Resolvable)
 * - Encapsulation
 * - Collections (ArrayList for history)
 * - Comparable for PriorityQueue sorting
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Complaint implements Serializable, Trackable, Resolvable, Comparable<Complaint> {

    private static final long serialVersionUID = 1L;

    private String complaintId;
    private String citizenId;
    private String citizenName;
    private ComplaintCategory category;
    private String title;
    private String description;
    private String location;
    private String ward;
    private String city;
    private Priority priority;
    private ComplaintStatus status;
    private String departmentId;
    private String departmentName;
    private String assignedOfficerId;
    private String assignedOfficerName;
    private String imageUrl;
    private String resolutionNote;
    private String resolutionImageUrl;
    private boolean escalated;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private LocalDateTime deadline;
    private LocalDateTime resolvedAt;
    private int citizenRating;
    private String citizenFeedback;
    private List<ComplaintHistory> history;  // Collections: ArrayList

    public Complaint() {
        this.history = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.status = ComplaintStatus.SUBMITTED;
        this.priority = Priority.MEDIUM;
        this.escalated = false;
        this.citizenRating = 0;
    }

    public Complaint(String complaintId, String citizenId, String citizenName,
                     ComplaintCategory category, String title, String description,
                     String location, String ward, String city) {
        this();
        this.complaintId = complaintId;
        this.citizenId = citizenId;
        this.citizenName = citizenName;
        this.category = category;
        this.title = title;
        this.description = description;
        this.location = location;
        this.ward = ward;
        this.city = city;
        // Default deadline: 48 hours from submission
        this.deadline = LocalDateTime.now().plusHours(48);
        // Add initial history entry
        addHistoryEntry("SYSTEM", "Complaint submitted successfully", ComplaintStatus.SUBMITTED);
    }

    // Helper method to add history entries
    public void addHistoryEntry(String performedBy, String action, ComplaintStatus newStatus) {
        ComplaintHistory entry = new ComplaintHistory(
                performedBy, action, newStatus, LocalDateTime.now()
        );
        this.history.add(entry);
        this.lastUpdated = LocalDateTime.now();
        this.status = newStatus;
    }

    // Trackable interface implementation
    @Override
    public String getComplaintId() { return complaintId; }

    @Override
    public ComplaintStatus getStatus() { return status; }

    @Override
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    @Override
    public String getTrackingSummary() {
        return String.format("Complaint %s | Status: %s | Priority: %s | Category: %s",
                complaintId, status, priority, category.getDisplayName());
    }

    // Resolvable interface implementation
    @Override
    public boolean resolve(String resolutionNote, String officerId) {
        if (this.assignedOfficerId != null && this.assignedOfficerId.equals(officerId)) {
            this.resolutionNote = resolutionNote;
            this.resolvedAt = LocalDateTime.now();
            addHistoryEntry(officerId, "Complaint resolved: " + resolutionNote, ComplaintStatus.RESOLVED);
            return true;
        }
        return false;
    }

    @Override
    public boolean isResolved() {
        return status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CLOSED;
    }

    @Override
    public String getResolutionNote() { return resolutionNote; }

    // Comparable for PriorityQueue (higher priority = higher order)
    @Override
    public int compareTo(Complaint other) {
        return Integer.compare(other.priority.getLevel(), this.priority.getLevel());
    }

    // Check if complaint is overdue
    public boolean isOverdue() {
        return !isResolved() && LocalDateTime.now().isAfter(deadline);
    }

    // String operations
    public String getShortDescription() {
        if (description != null && description.length() > 100) {
            return description.substring(0, 100) + "...";
        }
        return description;
    }

    // All getters and setters
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

    public String getCitizenName() { return citizenName; }
    public void setCitizenName(String citizenName) { this.citizenName = citizenName; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getAssignedOfficerId() { return assignedOfficerId; }
    public void setAssignedOfficerId(String assignedOfficerId) { this.assignedOfficerId = assignedOfficerId; }

    public String getAssignedOfficerName() { return assignedOfficerName; }
    public void setAssignedOfficerName(String assignedOfficerName) { this.assignedOfficerName = assignedOfficerName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public String getResolutionImageUrl() { return resolutionImageUrl; }
    public void setResolutionImageUrl(String resolutionImageUrl) { this.resolutionImageUrl = resolutionImageUrl; }

    public boolean isEscalated() { return escalated; }
    public void setEscalated(boolean escalated) { this.escalated = escalated; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public int getCitizenRating() { return citizenRating; }
    public void setCitizenRating(int citizenRating) { this.citizenRating = citizenRating; }

    public String getCitizenFeedback() { return citizenFeedback; }
    public void setCitizenFeedback(String citizenFeedback) { this.citizenFeedback = citizenFeedback; }

    public List<ComplaintHistory> getHistory() { return history; }
    public void setHistory(List<ComplaintHistory> history) { this.history = history; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complaint)) return false;
        Complaint c = (Complaint) o;
        return Objects.equals(complaintId, c.complaintId);
    }

    @Override
    public int hashCode() { return Objects.hash(complaintId); }

    @Override
    public String toString() {
        return String.format("Complaint{id='%s', category=%s, status=%s, priority=%s}",
                complaintId, category, status, priority);
    }
}
