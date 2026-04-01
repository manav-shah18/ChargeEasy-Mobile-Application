package com.example.chargeeasy;

/**
 * Data model representing a single user reservation/booking session.
 * Updated to use long timestamps for start and end times.
 */
public class BookingItem {
    private final String bookingId;
    private final String stationId;
    private final String stationName;
    private final String stationAddress;
    private final long startTimeMs; // <-- NEW
    private final long endTimeMs;   // <-- NEW
    private final int durationMinutes; // <-- NEW
    private final double cost;
    private boolean isCompleted;

    public BookingItem(String bookingId, String stationId, String stationName, String stationAddress,
                       long startTimeMs, long endTimeMs, int durationMinutes, // <-- NEW in constructor
                       double cost, boolean isCompleted) {
        this.bookingId = bookingId;
        this.stationId = stationId;
        this.stationName = stationName;
        this.stationAddress = stationAddress;
        this.startTimeMs = startTimeMs;
        this.endTimeMs = endTimeMs;
        this.durationMinutes = durationMinutes;
        this.cost = cost;
        this.isCompleted = isCompleted;
    }

    // --- Getters ---
    public String getBookingId() { return bookingId; }
    public String getStationId() { return stationId; }
    public String getStationName() { return stationName; }
    public String getStationAddress() { return stationAddress; }
    public long getStartTimeMs() { return startTimeMs; }
    public long getEndTimeMs() { return endTimeMs; }
    public int getDurationMinutes() { return durationMinutes; }
    public double getCost() { return cost; }
    public boolean isCompleted() { return isCompleted; }

    // --- Helper Getters for Display ---
    public String getBookingDate() {
        return TimeUtils.formatDate(this.startTimeMs);
    }
    public String getBookingTime() {
        return TimeUtils.formatTime(this.startTimeMs);
    }
    public String getDurationString() {
        return this.durationMinutes + " Min";
    }

    // --- Setter ---
    public void setCompleted(boolean completed) { isCompleted = completed; }
}