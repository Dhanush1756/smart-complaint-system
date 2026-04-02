package com.smartcms.controller;

import com.smartcms.dto.Dtos.*;
import com.smartcms.model.*;
import com.smartcms.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AdminController - admin-specific endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final AuditService auditService;

    public AdminController(UserService userService,
                           DepartmentService departmentService,
                           AuditService auditService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.auditService = auditService;
    }

    // ── Officers ────────────────────────────────────────────────────────────

    /** POST /api/admin/officers - Create officer */
    @PostMapping("/officers")
    public ResponseEntity<Officer> createOfficer(
            @RequestBody OfficerRegistrationRequest req,
            @RequestParam String adminId) {
        return ResponseEntity.ok(userService.registerOfficer(req, adminId));
    }

    /** GET /api/admin/officers - Get all officers */
    @GetMapping("/officers")
    public ResponseEntity<List<Officer>> getAllOfficers(
            @RequestParam(required = false) String departmentId) {
        if (departmentId != null) {
            return ResponseEntity.ok(userService.getOfficersByDepartment(departmentId));
        }
        return ResponseEntity.ok(userService.getAllOfficers());
    }

    /** GET /api/admin/officers/available - Get available officers for a dept */
    @GetMapping("/officers/available")
    public ResponseEntity<List<Officer>> getAvailableOfficers(@RequestParam String departmentId) {
        return ResponseEntity.ok(userService.getAvailableOfficers(departmentId));
    }

    /** DELETE /api/admin/officers/{id} - Deactivate officer */
    @DeleteMapping("/officers/{id}")
    public ResponseEntity<Map<String, Object>> deactivateOfficer(
            @PathVariable String id,
            @RequestParam String adminId) {
        boolean success = userService.deactivateUser(id, adminId);
        return ResponseEntity.ok(Map.of("success", success, "message", "Officer deactivated"));
    }

    // ── Departments ─────────────────────────────────────────────────────────

    /** POST /api/admin/departments */
    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(
            @RequestBody DepartmentRequest req,
            @RequestParam String adminId) {
        return ResponseEntity.ok(departmentService.createDepartment(req, adminId));
    }

    /** GET /api/admin/departments */
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    /** PUT /api/admin/departments/{id} */
    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable String id,
            @RequestBody DepartmentRequest req,
            @RequestParam String adminId) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, req, adminId));
    }

    /** DELETE /api/admin/departments/{id} */
    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Map<String, Object>> deleteDepartment(
            @PathVariable String id,
            @RequestParam String adminId) {
        boolean success = departmentService.deleteDepartment(id, adminId);
        return ResponseEntity.ok(Map.of("success", success));
    }

    // ── Citizens ─────────────────────────────────────────────────────────────

    /** GET /api/admin/citizens */
    @GetMapping("/citizens")
    public ResponseEntity<List<User>> getAllCitizens() {
        return ResponseEntity.ok(userService.getAllCitizens());
    }

    // ── Audit Logs ───────────────────────────────────────────────────────────

    /** GET /api/admin/audit-logs */
    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(auditService.getRecentLogs(limit));
    }

    /** GET /api/admin/audit-logs/complaint/{complaintId} */
    @GetMapping("/audit-logs/complaint/{complaintId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsForComplaint(@PathVariable String complaintId) {
        return ResponseEntity.ok(auditService.getLogsByEntity(complaintId));
    }
}
