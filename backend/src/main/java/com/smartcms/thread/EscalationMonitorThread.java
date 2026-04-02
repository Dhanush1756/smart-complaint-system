package com.smartcms.thread;

import com.smartcms.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * EscalationMonitorThread - background daemon thread.
 * Demonstrates Multithreading: Runnable interface, daemon threads,
 * synchronized sleep, scheduled execution.
 */
@Component
public class EscalationMonitorThread implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(EscalationMonitorThread.class);

    @Value("${app.escalation.check-interval-minutes:30}")
    private int intervalMinutes;

    private final ComplaintService complaintService;

    // Thread state (Multithreading: volatile for visibility across threads)
    private volatile boolean running = true;

    public EscalationMonitorThread(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @Override
    public void run() {
        log.info("EscalationMonitorThread started. Check interval: {} minutes", intervalMinutes);

        while (running) {
            try {
                // Run escalation check
                log.debug("Running escalation check...");
                complaintService.escalateOverdueComplaints();

                // Sleep for configured interval (Multithreading: Thread.sleep)
                Thread.sleep((long) intervalMinutes * 60 * 1000);

            } catch (InterruptedException e) {
                // Restore interrupted status (best practice for InterruptedException)
                Thread.currentThread().interrupt();
                log.info("EscalationMonitorThread interrupted, stopping.");
                running = false;
            } catch (Exception e) {
                log.error("Error in escalation check: {}", e.getMessage());
                // Continue running even if one check fails
                try {
                    Thread.sleep(60_000); // Wait 1 minute before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }

        log.info("EscalationMonitorThread stopped.");
    }

    public void stop() {
        this.running = false;
    }
}
