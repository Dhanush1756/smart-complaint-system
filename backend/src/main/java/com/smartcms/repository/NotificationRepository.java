package com.smartcms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartcms.model.Notification;
import com.smartcms.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * NotificationRepository.
 * Uses LinkedBlockingQueue for pending notifications (Multithreading / Collections).
 */
@Repository
public class NotificationRepository {

    @Value("${app.storage.notifications-file}")
    private String notifFilePath;

    private final FileStorageUtil fileStorage;
    private final Map<String, Notification> notifById = new HashMap<>();

    // Thread-safe queue for pending dispatch (Multithreading: BlockingQueue)
    private final LinkedBlockingQueue<Notification> pendingQueue = new LinkedBlockingQueue<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public NotificationRepository(FileStorageUtil fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void loadFromFile() {
        lock.writeLock().lock();
        try {
            List<Notification> notifs = fileStorage.loadFromFile(notifFilePath,
                    new TypeReference<List<Notification>>() {});
            notifById.clear();
            pendingQueue.clear();
            for (Notification n : notifs) {
                notifById.put(n.getId(), n);
                if (!n.isSent()) pendingQueue.offer(n);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveToFile() {
        lock.readLock().lock();
        try {
            fileStorage.saveToFile(notifFilePath, new ArrayList<>(notifById.values()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(Notification notif) {
        lock.writeLock().lock();
        try {
            notifById.put(notif.getId(), notif);
            if (!notif.isSent()) pendingQueue.offer(notif);
            saveToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Notification> findByRecipientId(String recipientId) {
        lock.readLock().lock();
        try {
            return notifById.values().stream()
                    .filter(n -> recipientId.equals(n.getRecipientId()))
                    .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<Notification> findUnreadByRecipientId(String recipientId) {
        lock.readLock().lock();
        try {
            return notifById.values().stream()
                    .filter(n -> recipientId.equals(n.getRecipientId()) && !n.isRead())
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /** Used by NotificationDispatchThread */
    public LinkedBlockingQueue<Notification> getPendingQueue() {
        return pendingQueue;
    }

    public void markRead(String notifId) {
        lock.writeLock().lock();
        try {
            Notification n = notifById.get(notifId);
            if (n != null) {
                n.setRead(true);
                saveToFile();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void markSent(String notifId) {
        lock.writeLock().lock();
        try {
            Notification n = notifById.get(notifId);
            if (n != null) {
                n.setSent(true);
                saveToFile();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long countUnread(String recipientId) {
        lock.readLock().lock();
        try {
            return notifById.values().stream()
                    .filter(n -> recipientId.equals(n.getRecipientId()) && !n.isRead())
                    .count();
        } finally {
            lock.readLock().unlock();
        }
    }
}
