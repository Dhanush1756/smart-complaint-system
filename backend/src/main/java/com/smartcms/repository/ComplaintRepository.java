package com.smartcms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartcms.model.Complaint;
import com.smartcms.model.ComplaintStatus;
import com.smartcms.model.Priority;
import com.smartcms.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * ComplaintRepository - file-based persistence.
 * Demonstrates Collections Framework extensively:
 * - HashMap for O(1) complaint lookup
 * - PriorityQueue for priority-ordered complaint processing
 * - ArrayList for filtered result sets
 * - TreeMap for sorted category statistics
 * - ReadWriteLock for thread safety (Multithreading)
 */
@Repository
public class ComplaintRepository {

    @Value("${app.storage.complaints-file}")
    private String complaintsFilePath;

    private final FileStorageUtil fileStorage;

    // Collections: HashMap for fast lookup
    private final Map<String, Complaint> complaintsById = new HashMap<>();

    // Collections: PriorityQueue - processes highest priority complaints first
    private final PriorityQueue<Complaint> priorityQueue = new PriorityQueue<>();

    // ReadWriteLock for thread safety
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ComplaintRepository(FileStorageUtil fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void loadFromFile() {
        lock.writeLock().lock();
        try {
            List<Complaint> complaints = fileStorage.loadFromFile(complaintsFilePath,
                    new TypeReference<List<Complaint>>() {});
            complaintsById.clear();
            priorityQueue.clear();
            for (Complaint c : complaints) {
                complaintsById.put(c.getComplaintId(), c);
                if (!c.isResolved()) priorityQueue.offer(c);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveToFile() {
        lock.readLock().lock();
        try {
            fileStorage.saveToFile(complaintsFilePath, new ArrayList<>(complaintsById.values()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(Complaint complaint) {
        lock.writeLock().lock();
        try {
            complaintsById.put(complaint.getComplaintId(), complaint);
            // Refresh priority queue
            priorityQueue.remove(complaint);
            if (!complaint.isResolved()) priorityQueue.offer(complaint);
            saveToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Complaint> findById(String id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(complaintsById.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Complaint> findAll() {
        lock.readLock().lock();
        try { return new ArrayList<>(complaintsById.values()); }
        finally { lock.readLock().unlock(); }
    }

    public List<Complaint> findByCitizenId(String citizenId) {
        lock.readLock().lock();
        try {
            return complaintsById.values().stream()
                    .filter(c -> citizenId.equals(c.getCitizenId()))
                    .sorted(Comparator.comparing(Complaint::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Complaint> findByStatus(ComplaintStatus status) {
        lock.readLock().lock();
        try {
            return complaintsById.values().stream()
                    .filter(c -> c.getStatus() == status)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Complaint> findByOfficerId(String officerId) {
        lock.readLock().lock();
        try {
            return complaintsById.values().stream()
                    .filter(c -> officerId.equals(c.getAssignedOfficerId()))
                    .sorted(Comparator.comparing(Complaint::getPriority,
                            Comparator.comparingInt(Priority::getLevel).reversed()))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Complaint> findByDepartmentId(String departmentId) {
        lock.readLock().lock();
        try {
            return complaintsById.values().stream()
                    .filter(c -> departmentId.equals(c.getDepartmentId()))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Returns overdue unresolved complaints (used by EscalationMonitorThread) */
    public List<Complaint> findOverdueComplaints() {
        lock.readLock().lock();
        try {
            LocalDateTime now = LocalDateTime.now();
            return complaintsById.values().stream()
                    .filter(c -> !c.isResolved()
                            && c.getDeadline() != null
                            && now.isAfter(c.getDeadline())
                            && !c.isEscalated())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /** PriorityQueue: peek at the highest-priority pending complaint */
    public Optional<Complaint> peekHighestPriority() {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(priorityQueue.peek());
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Collections: TreeMap - complaint counts by category (sorted alphabetically) */
    public TreeMap<String, Long> countByCategory() {
        lock.readLock().lock();
        try {
            TreeMap<String, Long> result = new TreeMap<>();
            complaintsById.values().forEach(c -> {
                String cat = c.getCategory().getDisplayName();
                result.merge(cat, 1L, Long::sum);
            });
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Collections: HashMap - complaint counts by status */
    public Map<String, Long> countByStatus() {
        lock.readLock().lock();
        try {
            Map<String, Long> result = new HashMap<>();
            for (ComplaintStatus s : ComplaintStatus.values()) result.put(s.name(), 0L);
            complaintsById.values().forEach(c ->
                    result.merge(c.getStatus().name(), 1L, Long::sum));
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Collections: HashMap - complaint counts by ward */
    public Map<String, Long> countByWard() {
        lock.readLock().lock();
        try {
            Map<String, Long> result = new HashMap<>();
            complaintsById.values().forEach(c -> {
                String ward = c.getWard() != null ? c.getWard() : "Unknown";
                result.merge(ward, 1L, Long::sum);
            });
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int count() {
        lock.readLock().lock();
        try { return complaintsById.size(); }
        finally { lock.readLock().unlock(); }
    }

    public boolean delete(String id) {
        lock.writeLock().lock();
        try {
            Complaint removed = complaintsById.remove(id);
            if (removed != null) {
                priorityQueue.remove(removed);
                saveToFile();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
