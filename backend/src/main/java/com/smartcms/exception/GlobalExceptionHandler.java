package com.smartcms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler - demonstrates Exception Handling in Spring Boot.
 * Catches custom exceptions and returns proper HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> errorBody(String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", error);
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(ComplaintNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleComplaintNotFound(ComplaintNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("Complaint Not Found", ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("User Not Found", ex.getMessage()));
    }

    @ExceptionHandler(InvalidComplaintException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidComplaint(InvalidComplaintException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorBody("Invalid Complaint", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorBody("Unauthorized", ex.getMessage()));
    }

    @ExceptionHandler(OfficerUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleOfficerUnavailable(OfficerUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("Officer Unavailable", ex.getMessage()));
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDeptNotFound(DepartmentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody("Department Not Found", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorBody("Duplicate Username", ex.getMessage()));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, Object>> handleStorage(StorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Storage Error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("Internal Server Error", ex.getMessage()));
    }
}
