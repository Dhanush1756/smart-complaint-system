package com.smartcms.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Admin class - inherits from User.
 * Demonstrates inheritance and polymorphism.
 * Uses HashSet for permissions (Collections).
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Admin extends User {

    private String designation;
    private Set<String> permissions;  // Collections: HashSet

    public Admin() {
        super();
        this.permissions = new HashSet<>();
        this.setRole(UserRole.ADMIN);
        // Default admin permissions
        this.permissions.add("MANAGE_COMPLAINTS");
        this.permissions.add("MANAGE_OFFICERS");
        this.permissions.add("MANAGE_DEPARTMENTS");
        this.permissions.add("VIEW_REPORTS");
        this.permissions.add("SYSTEM_CONFIG");
    }

    public Admin(String id, String username, String passwordHash, String email,
                 String phone, String fullName, String designation) {
        super(id, username, passwordHash, email, phone, fullName, UserRole.ADMIN);
        this.designation = designation;
        this.permissions = new HashSet<>();
        this.permissions.add("MANAGE_COMPLAINTS");
        this.permissions.add("MANAGE_OFFICERS");
        this.permissions.add("MANAGE_DEPARTMENTS");
        this.permissions.add("VIEW_REPORTS");
        this.permissions.add("SYSTEM_CONFIG");
    }

    @Override
    public String getDashboardData() {
        return String.format("Admin Dashboard - Designation: %s, Permissions: %s",
                designation, permissions.toString());
    }

    @Override
    public UserRole getUserType() {
        return UserRole.ADMIN;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public void addPermission(String permission) {
        permissions.add(permission);
    }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
