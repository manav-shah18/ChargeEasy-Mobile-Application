package com.example.chargeeasy;

/**
 * Data model representing a single EV charging station.
 * This data drives both the Map markers and the Station List.
 * FIX: isAvailable is now mutable to allow dynamic status updates.
 */
public class StationItem {
    private final String id;
    private final String name;
    private final String address;
    private final double latitude;
    private final double longitude;

    private boolean isAvailable; // <-- FIX: Removed 'final' keyword

    private final int numChargers;
    private final double maxPowerKw;
    private final double rating;
    private final int reviewCount;

    public StationItem(String id, String name, String address, double latitude, double longitude,
                       boolean isAvailable, int numChargers, double maxPowerKw, double rating,
                       int reviewCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isAvailable = isAvailable;
        this.numChargers = numChargers;
        this.maxPowerKw = maxPowerKw;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isAvailable() { return isAvailable; }
    public int getNumChargers() { return numChargers; }
    public double getMaxPowerKw() { return maxPowerKw; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }

    // --- SETTER (CRITICAL FIX) ---
    /**
     * Allows the map and list fragments to dynamically update the
     * station's availability based on real-time booking data.
     */
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return name + " (" + (isAvailable ? "Available" : "Busy") + ")";
    }
}