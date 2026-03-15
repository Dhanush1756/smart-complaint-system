package com.smartcms.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Citizen class demonstrating:
 * - Inheritance from User (extends User)
 * - Encapsulation
 * - Polymorphism (overrides abstract methods)
 * - Collections (ArrayList for complaint tracking)
 */
public class Citizen extends User {

    private String address;
    private String ward;
    private String city;
    private String pinCode;
    private List<String> complaintIds;  // Collections: ArrayList

    public Citizen() {
        super();
        this.complaintIds = new ArrayList<>();
        this.setRole(UserRole.CITIZEN);
    }

    public Citizen(String id, String username, String passwordHash, String email,
                   String phone, String fullName, String address, String ward, String city, String pinCode) {
        super(id, username, passwordHash, email, phone, fullName, UserRole.CITIZEN);
        this.address = address;
        this.ward = ward;
        this.city = city;
        this.pinCode = pinCode;
        this.complaintIds = new ArrayList<>();
    }

    // Polymorphism: overriding abstract method from User
    @Override
    public String getDashboardData() {
        return String.format("Citizen Dashboard - User: %s, Ward: %s, Complaints Filed: %d",
                getFullName(), ward, complaintIds.size());
    }

    @Override
    public UserRole getUserType() {
        return UserRole.CITIZEN;
    }

    // Notifiable interface implementation
    @Override
    public String getEmail() { return super.getEmail(); }

    @Override
    public String getPhone() { return super.getPhone(); }

    @Override
    public String getFullName() { return super.getFullName(); }

    // Citizen-specific methods
    public void addComplaintId(String complaintId) {
        if (!complaintIds.contains(complaintId)) {
            complaintIds.add(complaintId);
        }
    }

    public boolean removeComplaintId(String complaintId) {
        return complaintIds.remove(complaintId);
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public List<String> getComplaintIds() { return complaintIds; }
    public void setComplaintIds(List<String> complaintIds) { this.complaintIds = complaintIds; }

    @Override
    public String toString() {
        return String.format("Citizen{id='%s', name='%s', ward='%s', complaints=%d}",
                getId(), getFullName(), ward, complaintIds.size());
    }
}
