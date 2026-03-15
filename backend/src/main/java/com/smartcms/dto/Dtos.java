package com.smartcms.dto;

import com.smartcms.model.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Objects (DTOs) - separates API layer from model layer.
 * Demonstrates DTO pattern and encapsulation.
 */
public class Dtos {

    // ── Login ───────────────────────────────────────────────────────────────
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String userId;
        private String username;
        private String fullName;
        private String role;
        private String token;  // simple session token
        private String message;
        public LoginResponse() {}
        public LoginResponse(String userId, String username, String fullName, String role, String token) {
            this.userId = userId; this.username = username;
            this.fullName = fullName; this.role = role; this.token = token;
            this.message = "Login successful";
        }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // ── Citizen Registration ────────────────────────────────────────────────
    public static class CitizenRegistrationRequest {
        private String username;
        private String password;
        private String email;
        private String phone;
        private String fullName;
        private String address;
        private String ward;
        private String city;
        private String pinCode;
        public String getUsername() { return username; }
        public void setUsername(String v) { this.username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
        public String getEmail() { return email; }
        public void setEmail(String v) { this.email = v; }
        public String getPhone() { return phone; }
        public void setPhone(String v) { this.phone = v; }
        public String getFullName() { return fullName; }
        public void setFullName(String v) { this.fullName = v; }
        public String getAddress() { return address; }
        public void setAddress(String v) { this.address = v; }
        public String getWard() { return ward; }
        public void setWard(String v) { this.ward = v; }
        public String getCity() { return city; }
        public void setCity(String v) { this.city = v; }
        public String getPinCode() { return pinCode; }
        public void setPinCode(String v) { this.pinCode = v; }
    }

    // ── Officer Registration (by Admin) ────────────────────────────────────
    public static class OfficerRegistrationRequest {
        private String username;
        private String password;
        private String email;
        private String phone;
        private String fullName;
        private String employeeId;
        private String departmentId;
        private String designation;
        public String getUsername() { return username; }
        public void setUsername(String v) { this.username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
        public String getEmail() { return email; }
        public void setEmail(String v) { this.email = v; }
        public String getPhone() { return phone; }
        public void setPhone(String v) { this.phone = v; }
        public String getFullName() { return fullName; }
        public void setFullName(String v) { this.fullName = v; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String v) { this.employeeId = v; }
        public String getDepartmentId() { return departmentId; }
        public void setDepartmentId(String v) { this.departmentId = v; }
        public String getDesignation() { return designation; }
        public void setDesignation(String v) { this.designation = v; }
    }

    // ── Submit Complaint ────────────────────────────────────────────────────
    public static class ComplaintRequest {
        private String citizenId;
        private String category;
        private String title;
        private String description;
        private String location;
        private String ward;
        private String city;
        private String imageUrl;
        public String getCitizenId() { return citizenId; }
        public void setCitizenId(String v) { this.citizenId = v; }
        public String getCategory() { return category; }
        public void setCategory(String v) { this.category = v; }
        public String getTitle() { return title; }
        public void setTitle(String v) { this.title = v; }
        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
        public String getLocation() { return location; }
        public void setLocation(String v) { this.location = v; }
        public String getWard() { return ward; }
        public void setWard(String v) { this.ward = v; }
        public String getCity() { return city; }
        public void setCity(String v) { this.city = v; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String v) { this.imageUrl = v; }
    }

    // ── Assign Complaint ────────────────────────────────────────────────────
    public static class AssignRequest {
        private String departmentId;
        private String officerId;
        private String priority;
        private String adminId;
        public String getDepartmentId() { return departmentId; }
        public void setDepartmentId(String v) { this.departmentId = v; }
        public String getOfficerId() { return officerId; }
        public void setOfficerId(String v) { this.officerId = v; }
        public String getPriority() { return priority; }
        public void setPriority(String v) { this.priority = v; }
        public String getAdminId() { return adminId; }
        public void setAdminId(String v) { this.adminId = v; }
    }

    // ── Officer Update ──────────────────────────────────────────────────────
    public static class OfficerUpdateRequest {
        private String officerId;
        private String statusUpdate;
        private String progressNote;
        private String resolutionNote;
        private String resolutionImageUrl;
        public String getOfficerId() { return officerId; }
        public void setOfficerId(String v) { this.officerId = v; }
        public String getStatusUpdate() { return statusUpdate; }
        public void setStatusUpdate(String v) { this.statusUpdate = v; }
        public String getProgressNote() { return progressNote; }
        public void setProgressNote(String v) { this.progressNote = v; }
        public String getResolutionNote() { return resolutionNote; }
        public void setResolutionNote(String v) { this.resolutionNote = v; }
        public String getResolutionImageUrl() { return resolutionImageUrl; }
        public void setResolutionImageUrl(String v) { this.resolutionImageUrl = v; }
    }

    // ── Feedback ─────────────────────────────────────────────────────────
    public static class FeedbackRequest {
        private String citizenId;
        private int rating;
        private String feedback;
        public String getCitizenId() { return citizenId; }
        public void setCitizenId(String v) { this.citizenId = v; }
        public int getRating() { return rating; }
        public void setRating(int v) { this.rating = v; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String v) { this.feedback = v; }
    }

    // ── Department ──────────────────────────────────────────────────────────
    public static class DepartmentRequest {
        private String code;
        private String name;
        private String description;
        public String getCode() { return code; }
        public void setCode(String v) { this.code = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public String getDescription() { return description; }
        public void setDescription(String v) { this.description = v; }
    }

    // ── Analytics Summary ──────────────────────────────────────────────────
    public static class AnalyticsSummary {
        private long totalComplaints;
        private long pendingComplaints;
        private long resolvedComplaints;
        private long escalatedComplaints;
        private java.util.Map<String, Long> byCategory;
        private java.util.Map<String, Long> byStatus;
        private java.util.Map<String, Long> byWard;
        private double avgResolutionHours;

        public long getTotalComplaints() { return totalComplaints; }
        public void setTotalComplaints(long v) { this.totalComplaints = v; }
        public long getPendingComplaints() { return pendingComplaints; }
        public void setPendingComplaints(long v) { this.pendingComplaints = v; }
        public long getResolvedComplaints() { return resolvedComplaints; }
        public void setResolvedComplaints(long v) { this.resolvedComplaints = v; }
        public long getEscalatedComplaints() { return escalatedComplaints; }
        public void setEscalatedComplaints(long v) { this.escalatedComplaints = v; }
        public java.util.Map<String, Long> getByCategory() { return byCategory; }
        public void setByCategory(java.util.Map<String, Long> v) { this.byCategory = v; }
        public java.util.Map<String, Long> getByStatus() { return byStatus; }
        public void setByStatus(java.util.Map<String, Long> v) { this.byStatus = v; }
        public java.util.Map<String, Long> getByWard() { return byWard; }
        public void setByWard(java.util.Map<String, Long> v) { this.byWard = v; }
        public double getAvgResolutionHours() { return avgResolutionHours; }
        public void setAvgResolutionHours(double v) { this.avgResolutionHours = v; }
    }
}
