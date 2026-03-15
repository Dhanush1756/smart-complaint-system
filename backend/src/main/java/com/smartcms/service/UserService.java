package com.smartcms.service;

import com.smartcms.dto.Dtos.*;
import com.smartcms.exception.*;
import com.smartcms.model.*;
import com.smartcms.repository.DepartmentRepository;
import com.smartcms.repository.UserRepository;
import com.smartcms.util.IdGenerator;
import com.smartcms.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserService - business logic for user management.
 * Demonstrates Service layer, exception handling, and OOP usage.
 */
@Service
public class UserService {

    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final AuditService auditService;

    public UserService(UserRepository userRepo, DepartmentRepository deptRepo,
                       AuditService auditService) {
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.auditService = auditService;
    }

    // ── Authentication ──────────────────────────────────────────────────────

    public LoginResponse login(LoginRequest req) {
        // Exception handling: throw custom exception if not found
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UserNotFoundException("username: " + req.getUsername()));

        if (!user.isActive()) {
            throw new UnauthorizedAccessException("Account is deactivated");
        }

        if (!PasswordUtil.verifyPassword(req.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedAccessException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);

        auditService.log(user.getId(), user.getRole().name(), "LOGIN",
                "USER", user.getId(), "User logged in");

        // Simple token: base64 of userId:role:timestamp
        String token = java.util.Base64.getEncoder().encodeToString(
                (user.getId() + ":" + user.getRole() + ":" + System.currentTimeMillis()).getBytes()
        );

        return new LoginResponse(user.getId(), user.getUsername(), user.getFullName(),
                user.getRole().name(), token);
    }

    // ── Citizen Registration ────────────────────────────────────────────────

    public Citizen registerCitizen(CitizenRegistrationRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new DuplicateUsernameException(req.getUsername());
        }

        String id = IdGenerator.generateUserId("CITIZEN");
        Citizen citizen = new Citizen(
                id, req.getUsername(),
                PasswordUtil.hashPassword(req.getPassword()),
                req.getEmail(), req.getPhone(), req.getFullName(),
                req.getAddress(), req.getWard(), req.getCity(), req.getPinCode()
        );

        userRepo.save(citizen);
        auditService.log(id, "CITIZEN", "REGISTER", "USER", id,
                "Citizen registered: " + req.getUsername());
        return citizen;
    }

    // ── Officer Registration (by Admin) ────────────────────────────────────

    public Officer registerOfficer(OfficerRegistrationRequest req, String adminId) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new DuplicateUsernameException(req.getUsername());
        }

        Department dept = deptRepo.findById(req.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(req.getDepartmentId()));

        String id = IdGenerator.generateUserId("OFFICER");
        Officer officer = new Officer(
                id, req.getUsername(),
                PasswordUtil.hashPassword(req.getPassword()),
                req.getEmail(), req.getPhone(), req.getFullName(),
                req.getEmployeeId(), dept.getId(), dept.getName(), req.getDesignation()
        );

        userRepo.save(officer);
        dept.addOfficer(id);
        deptRepo.save(dept);

        auditService.log(adminId, "ADMIN", "CREATE_OFFICER", "USER", id,
                "Officer created: " + req.getUsername() + " in dept " + dept.getName());
        return officer;
    }

    // ── Profile ─────────────────────────────────────────────────────────────

    public User getUserById(String id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User updateProfile(String userId, String fullName, String phone,
                              String address, String ward) {
        User user = getUserById(userId);
        if (fullName != null) user.setFullName(fullName);
        if (phone != null)    user.setPhone(phone);
        if (user instanceof Citizen c) {
            if (address != null) c.setAddress(address);
            if (ward != null)    c.setWard(ward);
        }
        userRepo.save(user);
        auditService.log(userId, user.getRole().name(), "UPDATE_PROFILE", "USER", userId, "Profile updated");
        return user;
    }

    // ── Officer management ──────────────────────────────────────────────────

    public List<Officer> getAllOfficers() {
        return userRepo.findByRole(UserRole.OFFICER).stream()
                .map(u -> (Officer) u)
                .collect(Collectors.toList());
    }

    public List<Officer> getOfficersByDepartment(String departmentId) {
        return getAllOfficers().stream()
                .filter(o -> departmentId.equals(o.getDepartmentId()))
                .collect(Collectors.toList());
    }

    public List<Officer> getAvailableOfficers(String departmentId) {
        return getOfficersByDepartment(departmentId).stream()
                .filter(Officer::isAvailable)
                .collect(Collectors.toList());
    }

    public boolean deactivateUser(String userId, String adminId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepo.save(user);
        auditService.log(adminId, "ADMIN", "DEACTIVATE_USER", "USER", userId,
                "User deactivated: " + user.getUsername());
        return true;
    }

    public List<User> getAllCitizens() {
        return userRepo.findByRole(UserRole.CITIZEN);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
