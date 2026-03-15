package com.smartcms.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartcms.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File storage utility using JSON serialization.
 * Demonstrates:
 * - File handling (java.io)
 * - Serialization / Deserialization (Jackson JSON)
 * - Generics
 * - Exception handling (try-catch, custom exceptions)
 */
@Component
public class FileStorageUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);
    private final ObjectMapper objectMapper;

    public FileStorageUtil() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Saves a list of objects to a JSON file.
     * File handling: create directories if needed, write to file.
     */
    public <T> void saveToFile(String filePath, List<T> data) {
        try {
            File file = new File(filePath);
            // Create parent directories if they don't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writeValue(file, data);
            logger.debug("Saved {} records to {}", data.size(), filePath);
        } catch (IOException e) {
            logger.error("Failed to save data to {}: {}", filePath, e.getMessage());
            throw new StorageException("Failed to write to " + filePath, e);
        }
    }

    /**
     * Loads a list of objects from a JSON file.
     * Returns empty list if file doesn't exist.
     */
    public <T> List<T> loadFromFile(String filePath, TypeReference<List<T>> typeRef) {
        File file = new File(filePath);
        if (!file.exists()) {
            logger.debug("File not found, returning empty list: {}", filePath);
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            logger.error("Failed to load data from {}: {}", filePath, e.getMessage());
            throw new StorageException("Failed to read from " + filePath, e);
        }
    }

    /**
     * Saves a single object to a JSON file.
     */
    public <T> void saveObject(String filePath, T object) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writeValue(file, object);
        } catch (IOException e) {
            throw new StorageException("Failed to write object to " + filePath, e);
        }
    }

    /**
     * Loads a single object from a JSON file.
     */
    public <T> T loadObject(String filePath, Class<T> clazz) {
        File file = new File(filePath);
        if (!file.exists()) return null;
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            throw new StorageException("Failed to read object from " + filePath, e);
        }
    }

    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
}
