package com.smartcms.controller;

import com.smartcms.dto.Dtos.*;
import com.smartcms.model.Citizen;
import com.smartcms.model.User;
import com.smartcms.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AuthController - handles login and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** POST /api/auth/login */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse response = userService.login(req);
        return ResponseEntity.ok(response);
    }

    /** POST /api/auth/register */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody CitizenRegistrationRequest req) {
        Citizen citizen = userService.registerCitizen(req);
        return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "userId", citizen.getId(),
                "username", citizen.getUsername()
        ));
    }

    /** GET /api/auth/profile/{userId} */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    /** PUT /api/auth/profile/{userId} */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        User updated = userService.updateProfile(
                userId,
                body.get("fullName"),
                body.get("phone"),
                body.get("address"),
                body.get("ward")
        );
        return ResponseEntity.ok(updated);
    }
}
