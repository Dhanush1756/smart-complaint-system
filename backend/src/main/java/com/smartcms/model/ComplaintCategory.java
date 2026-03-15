package com.smartcms.model;

public enum ComplaintCategory {
    GARBAGE_COLLECTION("Garbage Collection", "SANITATION"),
    POTHOLE("Pothole / Road Damage", "ROADS"),
    BROKEN_STREETLIGHT("Broken Streetlight", "ELECTRICITY"),
    WATER_LEAKAGE("Water Leakage", "WATER"),
    DRAINAGE_ISSUE("Drainage Issue", "DRAINAGE"),
    ILLEGAL_DUMPING("Illegal Dumping", "SANITATION"),
    NOISE_COMPLAINT("Noise Complaint", "ENVIRONMENT"),
    TREE_HAZARD("Tree Hazard", "PARKS"),
    ENCROACHMENT("Encroachment", "ENFORCEMENT"),
    OTHER("Other", "GENERAL");

    private final String displayName;
    private final String defaultDepartmentCode;

    ComplaintCategory(String displayName, String defaultDepartmentCode) {
        this.displayName = displayName;
        this.defaultDepartmentCode = defaultDepartmentCode;
    }

    public String getDisplayName() { return displayName; }
    public String getDefaultDepartmentCode() { return defaultDepartmentCode; }
}
