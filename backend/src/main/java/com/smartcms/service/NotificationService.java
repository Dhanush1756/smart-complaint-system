package com.smartcms.service;

import com.smartcms.model.Notification;
import com.smartcms.model.Notification.NotificationType;
import com.smartcms.repository.NotificationRepository;
import com.smartcms.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NotificationService - creates and manages in-app notifications.
 * Works with NotificationDispatchThread for async delivery (Multithreading).
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notifRepo;

    public NotificationService(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    public Notification createNotification(String recipientId, String recipientEmail,
                                           String title, String message,
                                           String complaintId, NotificationType type) {
        Notification n = new Notification(
                IdGenerator.generateNotificationId(),
                recipientId, recipientEmail, title, message, complaintId, type
        );
        notifRepo.save(n);
        logger.info("Notification created for user {}: {}", recipientId, title);
        return n;
    }

    public void notifyStatusChange(String recipientId, String email,
                                   String complaintId, String newStatus) {
        createNotification(
                recipientId, email,
                "Complaint Status Updated",
                "Your complaint " + complaintId + " status changed to: " + newStatus,
                complaintId, NotificationType.STATUS_UPDATE
        );
    }

    public void notifyAssignment(String officerId, String officerEmail,
                                 String complaintId, String category) {
        createNotification(
                officerId, officerEmail,
                "New Complaint Assigned",
                "Complaint " + complaintId + " (" + category + ") has been assigned to you.",
                complaintId, NotificationType.ASSIGNMENT
        );
    }

    public void notifyEscalation(String adminId, String adminEmail, String complaintId) {
        createNotification(
                adminId, adminEmail,
                "⚠ Complaint Escalated",
                "Complaint " + complaintId + " has been escalated due to deadline breach.",
                complaintId, NotificationType.ESCALATION
        );
    }

    public void notifyResolution(String citizenId, String citizenEmail, String complaintId) {
        createNotification(
                citizenId, citizenEmail,
                "✓ Complaint Resolved",
                "Your complaint " + complaintId + " has been resolved. Please provide feedback.",
                complaintId, NotificationType.RESOLUTION
        );
    }

    public List<Notification> getNotificationsForUser(String userId) {
        return notifRepo.findByRecipientId(userId);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notifRepo.findUnreadByRecipientId(userId);
    }

    public long countUnread(String userId) {
        return notifRepo.countUnread(userId);
    }

    public void markRead(String notifId) {
        notifRepo.markRead(notifId);
    }
}
