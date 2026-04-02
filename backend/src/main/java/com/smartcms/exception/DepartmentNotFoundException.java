package com.smartcms.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(String deptId) {
        super("Department not found: " + deptId);
    }
}
