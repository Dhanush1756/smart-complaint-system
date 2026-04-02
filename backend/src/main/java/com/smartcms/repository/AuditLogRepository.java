package com.smartcms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartcms.model.AuditLog;
import com.smartcms.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AuditLogRepository - append-only log.
 * Uses CopyOnWriteArrayList for thread-safe reads without locking (Multithreading).
 */
@Repository
public class AuditLogRepository {

    @Value("${app.storage.audit-log-file}")
    private String auditFilePath;

    private final FileStorageUtil fileStorage;

    // Thread-safe list (Multithreading: CopyOnWriteArrayList)
    private final CopyOnWriteArrayList<AuditLog> logs = new CopyOnWriteArrayList<>();

    public AuditLogRepository(FileStorageUtil fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void loadFromFile() {
        List<AuditLog> loaded = fileStorage.loadFromFile(auditFilePath,
                new TypeReference<List<AuditLog>>() {});
        logs.clear();
        logs.addAll(loaded);
    }

    public void save(AuditLog log) {
        logs.add(log);
        fileStorage.saveToFile(auditFilePath, new ArrayList<>(logs));
    }

    public List<AuditLog> findAll() {
        return new ArrayList<>(logs);
    }

    public List<AuditLog> findByPerformedBy(String userId) {
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog l : logs) {
            if (userId.equals(l.getPerformedBy())) result.add(l);
        }
        return result;
    }

    public List<AuditLog> findByEntityId(String entityId) {
        List<AuditLog> result = new ArrayList<>();
        for (AuditLog l : logs) {
            if (entityId.equals(l.getEntityId())) result.add(l);
        }
        return result;
    }

    public List<AuditLog> findRecent(int limit) {
        int size = logs.size();
        int from = Math.max(0, size - limit);
        return new ArrayList<>(logs.subList(from, size));
    }
}
