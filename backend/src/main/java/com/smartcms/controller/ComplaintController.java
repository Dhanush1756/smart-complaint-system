package com.smartcms.controller;

import com.smartcms.dto.Dtos.*;
import com.smartcms.model.Complaint;
import com.smartcms.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComplaintController - REST API for all complaint operations.
 * Demonstrates Controller layer and REST API design.
 */
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /** POST /api/complaints - Submit a new complaint */
    @PostMapping
    public ResponseEntity<Complaint> submitComplaint(@RequestBody ComplaintRequest req) {
        Complaint complaint = complaintService.submitComplaint(req);
        return ResponseEntity.ok(complaint);
    }

    /** GET /api/complaints - Get all complaints (Admin) */
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints(
            @RequestParam(required = false) String status) {
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
        }
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    /** GET /api/complaints/{id} - Get complaint by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable String id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }

    /** GET /api/complaints/citizen/{citizenId} - Get complaints by citizen */
    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<Complaint>> getComplaintsByCitizen(@PathVariable String citizenId) {
        return ResponseEntity.ok(complaintService.getComplaintsByCitizen(citizenId));
    }

    /**
     * GET /api/complaints/officer/{officerId} - Get complaints assigned to officer
     */
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<Complaint>> getComplaintsByOfficer(@PathVariable String officerId) {
        return ResponseEntity.ok(complaintService.getComplaintsByOfficer(officerId));
    }

    /** PUT /api/complaints/{id}/assign - Admin assigns complaint */
    @PutMapping("/{id}/assign")
    public ResponseEntity<Complaint> assignComplaint(
            @PathVariable String id,
            @RequestBody AssignRequest req) {
        return ResponseEntity.ok(complaintService.assignComplaint(id, req));
    }

    /** PUT /api/complaints/{id}/update - Officer updates complaint */
    @PutMapping("/{id}/update")
    public ResponseEntity<Complaint> updateComplaint(
            @PathVariable String id,
            @RequestBody OfficerUpdateRequest req) {
        return ResponseEntity.ok(complaintService.updateComplaintProgress(id, req));
    }

    /** POST /api/complaints/{id}/feedback - Citizen submits feedback */
    @PostMapping("/{id}/feedback")
    public ResponseEntity<Complaint> submitFeedback(
            @PathVariable String id,
            @RequestBody FeedbackRequest req) {
        return ResponseEntity.ok(complaintService.submitFeedback(id, req));
    }

    /** GET /api/complaints/analytics - Analytics summary */
    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsSummary> getAnalytics() {
        return ResponseEntity.ok(complaintService.getAnalytics());
    }

    /** GET /api/complaints/{id}/track - Track complaint (Trackable interface) */
    @GetMapping("/{id}/track")
    public ResponseEntity<Map<String, Object>> trackComplaint(@PathVariable String id) {
        Complaint c = complaintService.getComplaintById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("complaintId", c.getComplaintId());
        result.put("status", c.getStatus());
        result.put("priority", c.getPriority());
        result.put("category", c.getCategory().getDisplayName());
        result.put("summary", c.getTrackingSummary());
        result.put("history", c.getHistory());
        result.put("isOverdue", c.isOverdue());
        result.put("isEscalated", c.isEscalated());
        result.put("department", c.getDepartmentName() != null ? c.getDepartmentName() : "Not assigned");
        result.put("officer", c.getAssignedOfficerName() != null ? c.getAssignedOfficerName() : "Not assigned");
        result.put("deadline", c.getDeadline() != null ? c.getDeadline().toString() : null);
        return ResponseEntity.ok(result);
    }
}