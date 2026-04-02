package com.smartcms.model;

/**
 * Interface demonstrating Interface concept in OOP.
 * Any class implementing this can receive notifications.
 */
public interface Notifiable {
    String getEmail();
    String getPhone();
    String getFullName();
}
