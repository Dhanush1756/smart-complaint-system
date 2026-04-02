package com.smartcms.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing utility.
 * Demonstrates static utility methods and Java security APIs.
 */
public class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {}

    public static String hashPassword(String plainPassword) {
        try {
            String salt = generateSalt();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(plainPassword.getBytes());
            String hashStr = Base64.getEncoder().encodeToString(hash);
            // Store salt:hash
            return salt + ":" + hashStr;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;
            String salt = parts[0];
            String existingHash = parts[1];
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(plainPassword.getBytes());
            String newHash = Base64.getEncoder().encodeToString(hash);
            return newHash.equals(existingHash);
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
