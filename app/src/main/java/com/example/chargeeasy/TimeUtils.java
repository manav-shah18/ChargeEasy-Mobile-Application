package com.example.chargeeasy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Utility class to handle all date/time formatting consistently.
 */
public class TimeUtils {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "h:mm a"; // 1:30 PM

    public static String formatDate(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String formatTime(long milliseconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return formatter.format(calendar.getTime());
    }

    public static String formatDate(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return formatter.format(calendar.getTime());
    }

    public static String formatTime(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.US);
        return formatter.format(calendar.getTime());
    }
}