package com.smartcms.model;

/**
 * Interface for resolvable complaints.
 * Demonstrates interface segregation principle.
 */
public interface Resolvable {
    boolean resolve(String resolutionNote, String officerId);
    boolean isResolved();
    String getResolutionNote();
}
