package com.smartcms.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.smartcms.model.Department;
import com.smartcms.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class DepartmentRepository {

    @Value("${app.storage.departments-file}")
    private String deptsFilePath;

    private final FileStorageUtil fileStorage;
    private final Map<String, Department> deptsById = new HashMap<>();
    private final Map<String, Department> deptsByCode = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public DepartmentRepository(FileStorageUtil fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void loadFromFile() {
        lock.writeLock().lock();
        try {
            List<Department> depts = fileStorage.loadFromFile(deptsFilePath,
                    new TypeReference<List<Department>>() {});
            deptsById.clear();
            deptsByCode.clear();
            for (Department d : depts) {
                deptsById.put(d.getId(), d);
                deptsByCode.put(d.getCode(), d);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveToFile() {
        lock.readLock().lock();
        try {
            fileStorage.saveToFile(deptsFilePath, new ArrayList<>(deptsById.values()));
        } finally {
            lock.readLock().unlock();
        }
    }

    public void save(Department dept) {
        lock.writeLock().lock();
        try {
            deptsById.put(dept.getId(), dept);
            deptsByCode.put(dept.getCode(), dept);
            saveToFile();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<Department> findById(String id) {
        lock.readLock().lock();
        try { return Optional.ofNullable(deptsById.get(id)); }
        finally { lock.readLock().unlock(); }
    }

    public Optional<Department> findByCode(String code) {
        lock.readLock().lock();
        try { return Optional.ofNullable(deptsByCode.get(code)); }
        finally { lock.readLock().unlock(); }
    }

    public List<Department> findAll() {
        lock.readLock().lock();
        try { return new ArrayList<>(deptsById.values()); }
        finally { lock.readLock().unlock(); }
    }

    public boolean delete(String id) {
        lock.writeLock().lock();
        try {
            Department removed = deptsById.remove(id);
            if (removed != null) {
                deptsByCode.remove(removed.getCode());
                saveToFile();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
