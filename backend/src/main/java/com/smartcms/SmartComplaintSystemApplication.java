package com.smartcms;

import com.smartcms.service.DataInitializerService;
import com.smartcms.thread.EscalationMonitorThread;
import com.smartcms.thread.NotificationDispatchThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Main entry point for the Smart Public Complaint Management System.
 * Demonstrates OOP concepts: package organization, class design, dependency injection.
 */
@SpringBootApplication
public class SmartComplaintSystemApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SmartComplaintSystemApplication.class, args);

        // Initialize sample data
        DataInitializerService initializer = context.getBean(DataInitializerService.class);
        initializer.initializeData();

        // Start background threads (Multithreading demonstration)
        EscalationMonitorThread escalationThread = context.getBean(EscalationMonitorThread.class);
        NotificationDispatchThread notificationThread = context.getBean(NotificationDispatchThread.class);

        Thread t1 = new Thread(escalationThread, "EscalationMonitor");
        Thread t2 = new Thread(notificationThread, "NotificationDispatcher");

        t1.setDaemon(true);
        t2.setDaemon(true);

        t1.start();
        t2.start();

        System.out.println("=======================================================");
        System.out.println("  SMART PUBLIC COMPLAINT MANAGEMENT SYSTEM STARTED");
        System.out.println("  API: http://localhost:8080/api");
        System.out.println("  Background threads: EscalationMonitor, NotificationDispatcher");
        System.out.println("=======================================================");
    }
}
