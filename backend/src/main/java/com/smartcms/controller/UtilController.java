package com.smartcms.controller;

import com.smartcms.model.ComplaintCategory;
import com.smartcms.model.ComplaintStatus;
import com.smartcms.model.Priority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/util")
public class UtilController {

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> list = new ArrayList<>();
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            list.add(Map.of(
                    "value", cat.name(),
                    "label", cat.getDisplayName(),
                    "department", cat.getDefaultDepartmentCode()
            ));
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getStatuses() {
        List<String> statuses = new ArrayList<>();
        for (ComplaintStatus s : ComplaintStatus.values()) statuses.add(s.name());
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<String>> getPriorities() {
        List<String> priorities = new ArrayList<>();
        for (Priority p : Priority.values()) priorities.add(p.name());
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "system", "Smart Public Complaint Management System",
                "version", "1.0.0"
        ));
    }
}
