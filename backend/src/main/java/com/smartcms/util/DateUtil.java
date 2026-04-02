package com.smartcms.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date utility - String operations and date formatting.
 */
public class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private DateUtil() {}

    public static String format(LocalDateTime dt) {
        if (dt == null) return "N/A";
        return dt.format(DISPLAY_FORMATTER);
    }

    public static long hoursBetween(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.HOURS.between(from, to);
    }

    public static String timeAgo(LocalDateTime dt) {
        if (dt == null) return "Unknown";
        long minutes = ChronoUnit.MINUTES.between(dt, LocalDateTime.now());
        if (minutes < 1)   return "Just now";
        if (minutes < 60)  return minutes + " min ago";
        long hours = minutes / 60;
        if (hours < 24)    return hours + " hour(s) ago";
        long days = hours / 24;
        if (days < 30)     return days + " day(s) ago";
        return dt.format(DISPLAY_FORMATTER);
    }
}
