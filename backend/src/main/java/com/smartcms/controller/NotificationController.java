package com.smartcms.controller;

import com.smartcms.model.Notification;
import com.smartcms.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notifService;

    public NotificationController(NotificationService notifService) {
        this.notifService = notifService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notifService.getNotificationsForUser(userId));
    }

    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notifService.getUnreadNotifications(userId));
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of("unreadCount", notifService.countUnread(userId)));
    }

    @PutMapping("/{notifId}/read")
    public ResponseEntity<Map<String, String>> markRead(@PathVariable String notifId) {
        notifService.markRead(notifId);
        return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    }
}
