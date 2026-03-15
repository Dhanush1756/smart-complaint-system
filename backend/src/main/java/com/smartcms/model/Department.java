package com.smartcms.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Department model.
 * Municipal department that handles specific complaint categories.
 */
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String code;
    private String name;
    private String description;
    private String headOfficerId;
    private List<String> officerIds;     // Collections: ArrayList
    private List<String> categories;    // Complaint categories this department handles
    private boolean active;
    private int totalComplaints;
    private int resolvedComplaints;

    public Department() {
        this.officerIds = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.active = true;
        this.totalComplaints = 0;
        this.resolvedComplaints = 0;
    }

    public Department(String id, String code, String name, String description) {
        this();
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public double getEfficiencyRate() {
        if (totalComplaints == 0) return 0.0;
        return ((double) resolvedComplaints / totalComplaints) * 100.0;
    }

    public void addOfficer(String officerId) {
        if (!officerIds.contains(officerId)) {
            officerIds.add(officerId);
        }
    }

    public void removeOfficer(String officerId) {
        officerIds.remove(officerId);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHeadOfficerId() { return headOfficerId; }
    public void setHeadOfficerId(String headOfficerId) { this.headOfficerId = headOfficerId; }

    public List<String> getOfficerIds() { return officerIds; }
    public void setOfficerIds(List<String> officerIds) { this.officerIds = officerIds; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(int totalComplaints) { this.totalComplaints = totalComplaints; }

    public int getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(int resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    @Override
    public String toString() {
        return String.format("Department{code='%s', name='%s', officers=%d}", code, name, officerIds.size());
    }
}
