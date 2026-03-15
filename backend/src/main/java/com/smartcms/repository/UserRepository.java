package com.smartcms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartcms.model.User;
import com.smartcms.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * UserRepository - file-based data layer.
 * Demonstrates:
 * - Repository pattern
 * - HashMap for fast lookups (Collections Framework)
 * - Thread-safe read/write with ReadWriteLock (Multithreading)
 * - Generics
 */
@Repository
public class UserRepository {

    @Value("${app.storage.users-file}")
    private String usersFilePath;

    private final FileStorageUtil fileStorage;

    // HashMap for O(1) lookup by userId (Collections: HashMap)
    private final Map<String, User> usersById = new HashMap<>();
    // HashMap for fast username lookup
    private final Map<String, User> usersByUsername = new HashMap<>();

    // ReadWriteLock for thread-safe concurrent access (Multithreading)
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public UserRepository(FileStorageUtil fileStorage) {
        this.fileStorage = fileStorage;
    }

    /** Load all users from file into in-memory maps */
    public void loadFromFile() {
        lock.writeLock().lock();
        try {
            List<User> users = fileStorage.loadFromFile(usersFilePath,
                    new TypeReference<List<User>>() {});
            usersById.clear();
            usersByUsername.clear();
            for (User u : users) {
                usersById.put(u.getId(), u);
                usersByUsername.put(u.getUsername().toLowerCase(), u);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /** Persist all users to file */
    public void saveToFile() {
        lock.readLock().lock();
        try {
            fileStorage.saveToFile(usersFilePath, new ArrayList<>(usersById.values()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(User user) {
        lock.writeLock().lock();
        try {
            usersById.put(user.getId(), user);
            usersByUsername.put(user.getUsername().toLowerCase(), user);
            saveToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<User> findById(String id) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(usersById.get(id));
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<User> findByUsername(String username) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean existsByUsername(String username) {
        lock.readLock().lock();
        try {
            return usersByUsername.containsKey(username.toLowerCase());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<User> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(usersById.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<User> findByRole(com.smartcms.model.UserRole role) {
        lock.readLock().lock();
        try {
            return usersById.values().stream()
                    .filter(u -> u.getRole() == role)
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean delete(String id) {
        lock.writeLock().lock();
        try {
            User removed = usersById.remove(id);
            if (removed != null) {
                usersByUsername.remove(removed.getUsername().toLowerCase());
                saveToFile();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int count() {
        lock.readLock().lock();
        try { return usersById.size(); }
        finally { lock.readLock().unlock(); }
    }
}
