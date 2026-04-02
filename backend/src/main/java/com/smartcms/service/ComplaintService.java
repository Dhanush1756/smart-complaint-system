package com.smartcms.service;

import com.smartcms.dto.Dtos.*;
import com.smartcms.exception.*;
import com.smartcms.model.*;
import com.smartcms.repository.ComplaintRepository;
import com.smartcms.repository.DepartmentRepository;
import com.smartcms.repository.UserRepository;
import com.smartcms.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ComplaintService - core business logic.
 * Demonstrates:
 * - Service layer pattern
 * - Exception handling (custom exceptions)
 * - Collections usage (HashMap, ArrayList, PriorityQueue via repo)
 * - String operations
 * - Polymorphism (User subtypes)
 */
@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepo;
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final NotificationService notifService;
    private final AuditService auditService;

    public ComplaintService(ComplaintRepository complaintRepo,
                            UserRepository userRepo,
                            DepartmentRepository deptRepo,
                            NotificationService notifService,
                            AuditService auditService) {
        this.complaintRepo = complaintRepo;
        this.userRepo = userRepo;
        this.deptRepo = deptRepo;
        this.notifService = notifService;
        this.auditService = auditService;
    }

    // ── Submit Complaint ────────────────────────────────────────────────────

    public Complaint submitComplaint(ComplaintRequest req) {
        // Validate citizen exists
        User user = userRepo.findById(req.getCitizenId())
                .orElseThrow(() -> new UserNotFoundException(req.getCitizenId()));

        if (!(user instanceof Citizen citizen)) {
            throw new InvalidComplaintException("Only citizens can submit complaints");
        }

        // Validate category
        ComplaintCategory category;
        try {
            category = ComplaintCategory.valueOf(req.getCategory().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidComplaintException("Invalid category: " + req.getCategory());
        }

        // Validate description length (String operations)
        if (req.getDescription() == null || req.getDescription().trim().length() < 10) {
            throw new InvalidComplaintException("Description must be at least 10 characters");
        }

        // Auto-generate complaint ID: CMP-2026-0001
        String complaintId = IdGenerator.generateComplaintId();

        Complaint complaint = new Complaint(
                complaintId,
                citizen.getId(),
                citizen.getFullName(),
                category,
                req.getTitle(),
                req.getDescription().trim(),
                req.getLocation(),
                req.getWard(),
                req.getCity()
        );

        if (req.getImageUrl() != null && !req.getImageUrl().isBlank()) {
            complaint.setImageUrl(req.getImageUrl());
        }

        // Auto-assign to department based on category
        Optional<Department> dept = deptRepo.findByCode(category.getDefaultDepartmentCode());
        dept.ifPresent(d -> {
            complaint.setDepartmentId(d.getId());
            complaint.setDepartmentName(d.getName());
            d.setTotalComplaints(d.getTotalComplaints() + 1);
            deptRepo.save(d);
            complaint.addHistoryEntry("SYSTEM",
                    "Auto-assigned to department: " + d.getName(),
                    ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
        });

        complaintRepo.save(complaint);
        citizen.addComplaintId(complaintId);
        userRepo.save(citizen);

        auditService.log(citizen.getId(), "CITIZEN", "SUBMIT_COMPLAINT",
                "COMPLAINT", complaintId,
                "Complaint submitted: " + category.getDisplayName());

        return complaint;
    }

    // ── Admin: Set Priority & Assign ────────────────────────────────────────

    public Complaint assignComplaint(String complaintId, AssignRequest req) {
        Complaint complaint = getComplaintById(complaintId);

        // Set priority
        if (req.getPriority() != null) {
            try {
                complaint.setPriority(Priority.valueOf(req.getPriority().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        // Assign to department
        if (req.getDepartmentId() != null) {
            Department dept = deptRepo.findById(req.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException(req.getDepartmentId()));
            complaint.setDepartmentId(dept.getId());
            complaint.setDepartmentName(dept.getName());
            complaint.addHistoryEntry(req.getAdminId(),
                    "Assigned to department: " + dept.getName(),
                    ComplaintStatus.ASSIGNED_TO_DEPARTMENT);
        }

        // Assign to officer
        if (req.getOfficerId() != null && !req.getOfficerId().isBlank()) {
            User officerUser = userRepo.findById(req.getOfficerId())
                    .orElseThrow(() -> new UserNotFoundException(req.getOfficerId()));

            if (!(officerUser instanceof Officer officer)) {
                throw new OfficerUnavailableException(req.getOfficerId());
            }

            complaint.setAssignedOfficerId(officer.getId());
            complaint.setAssignedOfficerName(officer.getFullName());
            complaint.addHistoryEntry(req.getAdminId(),
                    "Assigned to officer: " + officer.getFullName(),
                    ComplaintStatus.ASSIGNED_TO_OFFICER);

            officer.assignComplaint(complaintId);
            userRepo.save(officer);

            // Notify officer
            notifService.notifyAssignment(
                    officer.getId(), officer.getEmail(),
                    complaintId, complaint.getCategory().getDisplayName()
            );
        }

        // Notify citizen of status change
        userRepo.findById(complaint.getCitizenId()).ifPresent(u ->
                notifService.notifyStatusChange(u.getId(), u.getEmail(),
                        complaintId, complaint.getStatus().name())
        );

        complaintRepo.save(complaint);
        auditService.log(req.getAdminId(), "ADMIN", "ASSIGN_COMPLAINT",
                "COMPLAINT", complaintId, "Complaint assigned");
        return complaint;
    }

    // ── Officer: Update Progress ────────────────────────────────────────────

    public Complaint updateComplaintProgress(String complaintId, OfficerUpdateRequest req) {
        Complaint complaint = getComplaintById(complaintId);

        if (!req.getOfficerId().equals(complaint.getAssignedOfficerId())) {
            throw new UnauthorizedAccessException("Not assigned to this complaint");
        }

        String statusStr = req.getStatusUpdate();
        ComplaintStatus newStatus = complaint.getStatus();

        if ("IN_PROGRESS".equalsIgnoreCase(statusStr)) {
            newStatus = ComplaintStatus.IN_PROGRESS;
            String note = req.getProgressNote() != null ? req.getProgressNote() : "Work started";
            complaint.addHistoryEntry(req.getOfficerId(), "Progress: " + note, newStatus);
        } else if ("RESOLVED".equalsIgnoreCase(statusStr)) {
            if (req.getResolutionNote() == null || req.getResolutionNote().isBlank()) {
                throw new InvalidComplaintException("Resolution note is required");
            }
            newStatus = ComplaintStatus.RESOLVED;
            complaint.setResolutionNote(req.getResolutionNote());
            complaint.setResolvedAt(LocalDateTime.now());
            if (req.getResolutionImageUrl() != null) {
                complaint.setResolutionImageUrl(req.getResolutionImageUrl());
            }
            complaint.addHistoryEntry(req.getOfficerId(),
                    "Resolved: " + req.getResolutionNote(), newStatus);

            // Update officer stats
            userRepo.findById(req.getOfficerId()).ifPresent(u -> {
                if (u instanceof Officer o) {
                    o.resolveComplaint(complaintId);
                    userRepo.save(o);
                }
            });

            // Update dept stats
            if (complaint.getDepartmentId() != null) {
                deptRepo.findById(complaint.getDepartmentId()).ifPresent(d -> {
                    d.setResolvedComplaints(d.getResolvedComplaints() + 1);
                    deptRepo.save(d);
                });
            }

            // Notify citizen
            userRepo.findById(complaint.getCitizenId()).ifPresent(u ->
                    notifService.notifyResolution(u.getId(), u.getEmail(), complaintId)
            );
        }

        complaintRepo.save(complaint);
        auditService.log(req.getOfficerId(), "OFFICER", "UPDATE_COMPLAINT",
                "COMPLAINT", complaintId, "Status -> " + newStatus);
        return complaint;
    }

    // ── Citizen: Submit Feedback ────────────────────────────────────────────

    public Complaint submitFeedback(String complaintId, FeedbackRequest req) {
        Complaint complaint = getComplaintById(complaintId);

        if (!req.getCitizenId().equals(complaint.getCitizenId())) {
            throw new UnauthorizedAccessException("Cannot rate this complaint");
        }
        if (!complaint.isResolved()) {
            throw new InvalidComplaintException("Can only rate resolved complaints");
        }
        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new InvalidComplaintException("Rating must be between 1 and 5");
        }

        complaint.setCitizenRating(req.getRating());
        complaint.setCitizenFeedback(req.getFeedback());
        complaint.addHistoryEntry(req.getCitizenId(),
                "Citizen feedback submitted (Rating: " + req.getRating() + "/5)",
                ComplaintStatus.CLOSED);

        // Update officer rating
        if (complaint.getAssignedOfficerId() != null) {
            userRepo.findById(complaint.getAssignedOfficerId()).ifPresent(u -> {
                if (u instanceof Officer o) {
                    o.updateRatingWithFeedback(req.getRating());
                    userRepo.save(o);
                }
            });
        }

        complaintRepo.save(complaint);
        auditService.log(req.getCitizenId(), "CITIZEN", "SUBMIT_FEEDBACK",
                "COMPLAINT", complaintId, "Rating: " + req.getRating());
        return complaint;
    }

    // ── Escalation (called by EscalationMonitorThread) ─────────────────────

    public void escalateOverdueComplaints() {
        List<Complaint> overdue = complaintRepo.findOverdueComplaints();
        for (Complaint c : overdue) {
            c.setEscalated(true);
            c.addHistoryEntry("SYSTEM",
                    "Auto-escalated: deadline breached", ComplaintStatus.ESCALATED);
            complaintRepo.save(c);

            // Notify admin(s)
            userRepo.findByRole(UserRole.ADMIN).forEach(admin ->
                    notifService.notifyEscalation(admin.getId(), admin.getEmail(), c.getComplaintId())
            );

            auditService.log("SYSTEM", "SYSTEM", "ESCALATE",
                    "COMPLAINT", c.getComplaintId(), "Auto-escalated due to deadline breach");
        }
    }

    // ── Queries ─────────────────────────────────────────────────────────────

    public Complaint getComplaintById(String id) {
        return complaintRepo.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException(id));
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepo.findAll();
    }

    public List<Complaint> getComplaintsByCitizen(String citizenId) {
        return complaintRepo.findByCitizenId(citizenId);
    }

    public List<Complaint> getComplaintsByOfficer(String officerId) {
        return complaintRepo.findByOfficerId(officerId);
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        try {
            return complaintRepo.findByStatus(ComplaintStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new InvalidComplaintException("Unknown status: " + status);
        }
    }

    // ── Analytics ──────────────────────────────────────────────────────────

    public AnalyticsSummary getAnalytics() {
        List<Complaint> all = complaintRepo.findAll();
        AnalyticsSummary summary = new AnalyticsSummary();

        summary.setTotalComplaints(all.size());
        summary.setResolvedComplaints(
                all.stream().filter(Complaint::isResolved).count());
        summary.setPendingComplaints(
                all.stream().filter(c -> !c.isResolved()).count());
        summary.setEscalatedComplaints(
                all.stream().filter(Complaint::isEscalated).count());
        summary.setByCategory(complaintRepo.countByCategory());
        summary.setByStatus(complaintRepo.countByStatus());
        summary.setByWard(complaintRepo.countByWard());

        // Avg resolution time (String + stream operations)
        OptionalDouble avg = all.stream()
                .filter(c -> c.getResolvedAt() != null && c.getCreatedAt() != null)
                .mapToLong(c -> ChronoUnit.HOURS.between(c.getCreatedAt(), c.getResolvedAt()))
                .average();
        summary.setAvgResolutionHours(avg.orElse(0.0));

        return summary;
    }
}
