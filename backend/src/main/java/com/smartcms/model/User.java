package com.smartcms.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class demonstrating:
 * - Abstraction
 * - Encapsulation (private fields with getters/setters)
 * - Inheritance (Citizen, Admin, Officer extend this)
 * - Serializable for file persistence
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "userType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Citizen.class, name = "CITIZEN"),
    @JsonSubTypes.Type(value = Admin.class, name = "ADMIN"),
    @JsonSubTypes.Type(value = Officer.class, name = "OFFICER")
})
public abstract class User implements Serializable, Notifiable {

    private static final long serialVersionUID = 1L;

    // Encapsulated fields
    private String id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String fullName;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private UserRole role;

    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Parameterized constructor
    public User(String id, String username, String passwordHash, String email,
                String phone, String fullName, UserRole role) {
        this();
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.role = role;
    }

    // Abstract method - forces subclasses to provide specific behavior (Polymorphism)
    public abstract String getDashboardData();
    public abstract UserRole getUserType();

    // Encapsulated getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    // Overriding Object methods (Polymorphism)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', username='%s', role=%s}", id, username, role);
    }
}
