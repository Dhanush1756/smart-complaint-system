package com.smartcms.util;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for generating unique IDs.
 * Demonstrates:
 * - Static methods / utility pattern
 * - Thread-safe AtomicInteger for concurrent access (Multithreading)
 * - String operations / formatting
 */
public class IdGenerator {

    // Thread-safe counter using AtomicInteger (Multithreading concept)
    private static final AtomicInteger complaintCounter = new AtomicInteger(1);
    private static final AtomicInteger userCounter = new AtomicInteger(1);
    private static final AtomicInteger notificationCounter = new AtomicInteger(1);
    private static final AtomicInteger auditCounter = new AtomicInteger(1);

    private IdGenerator() { /* prevent instantiation */ }

    /**
     * Generates complaint ID in format: CMP-YYYY-NNNN
     * Example: CMP-2026-0001
     */
    public static String generateComplaintId() {
        int year = LocalDateTime.now().getYear();
        int seq = complaintCounter.getAndIncrement();
        // String.format demonstrates String operations
        return String.format("CMP-%d-%04d", year, seq);
    }

    public static String generateUserId(String role) {
        int seq = userCounter.getAndIncrement();
        String prefix = switch (role.toUpperCase()) {
            case "CITIZEN" -> "CIT";
            case "ADMIN"   -> "ADM";
            case "OFFICER" -> "OFF";
            default        -> "USR";
        };
        return String.format("%s-%04d", prefix, seq);
    }

    public static String generateNotificationId() {
        return String.format("NTF-%05d", notificationCounter.getAndIncrement());
    }

    public static String generateAuditId() {
        return String.format("AUD-%06d", auditCounter.getAndIncrement());
    }

    public static String generateDepartmentId() {
        return "DEPT-" + System.currentTimeMillis();
    }

    /** Initialize counters from existing data to avoid duplicates after restart */
    public static void initComplaintCounter(int nextVal) {
        complaintCounter.set(nextVal);
    }

    public static void initUserCounter(int nextVal) {
        userCounter.set(nextVal);
    }
}
