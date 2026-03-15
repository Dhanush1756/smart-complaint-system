package com.smartcms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Officer class - inherits from User.
 * Municipal staff who resolve complaints.
 * Demonstrates inheritance, encapsulation, collections.
 */
public class Officer extends User {

    private String employeeId;
    private String departmentId;
    private String departmentName;
    private String designation;
    private boolean available;
    private int resolvedCount;
    private int totalAssigned;
    private double performanceRating;
    private List<String> assignedComplaintIds;  // Collections: ArrayList

    public Officer() {
        super();
        this.assignedComplaintIds = new ArrayList<>();
        this.available = true;
        this.resolvedCount = 0;
        this.totalAssigned = 0;
        this.performanceRating = 5.0;
        this.setRole(UserRole.OFFICER);
    }

    public Officer(String id, String username, String passwordHash, String email,
                   String phone, String fullName, String employeeId,
                   String departmentId, String departmentName, String designation) {
        super(id, username, passwordHash, email, phone, fullName, UserRole.OFFICER);
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.designation = designation;
        this.assignedComplaintIds = new ArrayList<>();
        this.available = true;
        this.resolvedCount = 0;
        this.totalAssigned = 0;
        this.performanceRating = 5.0;
    }

    @Override
    public String getDashboardData() {
        return String.format("Officer Dashboard - %s (%s), Department: %s, Resolved: %d/%d, Rating: %.1f",
                getFullName(), designation, departmentName, resolvedCount, totalAssigned, performanceRating);
    }

    @Override
    public UserRole getUserType() {
        return UserRole.OFFICER;
    }

    // Officer-specific methods
    public void assignComplaint(String complaintId) {
        if (!assignedComplaintIds.contains(complaintId)) {
            assignedComplaintIds.add(complaintId);
            totalAssigned++;
        }
    }

    public void resolveComplaint(String complaintId) {
        if (assignedComplaintIds.contains(complaintId)) {
            resolvedCount++;
            updatePerformanceRating();
        }
    }

    // Calculate performance rating based on resolution rate
    private void updatePerformanceRating() {
        if (totalAssigned > 0) {
            double resolutionRate = (double) resolvedCount / totalAssigned;
            // Rating between 1 and 10
            this.performanceRating = Math.min(10.0, resolutionRate * 10.0);
        }
    }

    public void updateRatingWithFeedback(int feedbackRating) {
        // Weighted average: 70% performance, 30% feedback
        this.performanceRating = (this.performanceRating * 0.7) + (feedbackRating * 0.3);
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(int resolvedCount) { this.resolvedCount = resolvedCount; }

    public int getTotalAssigned() { return totalAssigned; }
    public void setTotalAssigned(int totalAssigned) { this.totalAssigned = totalAssigned; }

    public double getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(double performanceRating) { this.performanceRating = performanceRating; }

    public List<String> getAssignedComplaintIds() { return assignedComplaintIds; }
    public void setAssignedComplaintIds(List<String> assignedComplaintIds) {
        this.assignedComplaintIds = assignedComplaintIds;
    }
}
