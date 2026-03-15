package com.smartcms.thread;

import com.smartcms.model.Notification;
import com.smartcms.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * NotificationDispatchThread - processes pending notifications asynchronously.
 * Demonstrates Multithreading:
 * - Runnable interface
 * - BlockingQueue (producer-consumer pattern)
 * - Thread-safe queue operations
 * - Daemon thread pattern
 */
@Component
public class NotificationDispatchThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchThread.class);

    private final NotificationRepository notifRepo;
    private volatile boolean running = true;

    public NotificationDispatchThread(NotificationRepository notifRepo) {
        this.notifRepo = notifRepo;
    }

    @Override
    public void run() {
        log.info("NotificationDispatchThread started (producer-consumer pattern).");
        BlockingQueue<Notification> queue = notifRepo.getPendingQueue();

        while (running) {
            try {
                // BlockingQueue.poll with timeout - doesn't block forever
                // (Multithreading: blocking queue consumer)
                Notification notif = queue.poll(5, TimeUnit.SECONDS);

                if (notif != null && !notif.isSent()) {
                    dispatch(notif);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("NotificationDispatchThread interrupted.");
                running = false;
            } catch (Exception e) {
                log.error("Error dispatching notification: {}", e.getMessage());
            }
        }

        log.info("NotificationDispatchThread stopped.");
    }

    /**
     * Simulates sending notification.
     * In production: integrate email (JavaMailSender) or SMS gateway.
     */
    private void dispatch(Notification notif) {
        // Simulate processing delay
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        log.info("[NOTIFICATION] To: {} | Type: {} | Subject: {} | Message: {}",
                notif.getRecipientEmail(),
                notif.getType(),
                notif.getTitle(),
                notif.getMessage());

        notif.setSent(true);
        notif.setSentAt(LocalDateTime.now());
        notifRepo.markSent(notif.getId());
    }

    public void stop() {
        this.running = false;
    }
}
