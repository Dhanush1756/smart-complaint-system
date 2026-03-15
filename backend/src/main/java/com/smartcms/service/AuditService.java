package com.smartcms.service;

import com.smartcms.model.AuditLog;
import com.smartcms.repository.AuditLogRepository;
import com.smartcms.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AuditService - records all system actions for traceability.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditRepo;

    public AuditService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    public void log(String performedBy, String userRole, String action,
                    String entityType, String entityId, String details) {
        AuditLog entry = new AuditLog(
                IdGenerator.generateAuditId(),
                performedBy, userRole, action, entityType, entityId, details
        );
        auditRepo.save(entry);
    }

    public List<AuditLog> getAllLogs() {
        return auditRepo.findAll();
    }

    public List<AuditLog> getLogsByUser(String userId) {
        return auditRepo.findByPerformedBy(userId);
    }

    public List<AuditLog> getLogsByEntity(String entityId) {
        return auditRepo.findByEntityId(entityId);
    }

    public List<AuditLog> getRecentLogs(int limit) {
        return auditRepo.findRecent(limit);
    }
}
