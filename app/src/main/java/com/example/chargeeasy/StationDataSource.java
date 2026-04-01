package com.example.chargeeasy;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides hardcoded station data for testing the Map and List views,
 * and includes utility methods for retrieving single stations.
 */
public class StationDataSource {

    /**
     * Provides the static mock list of charging stations.
     */
    public static List<StationItem> getMockStations() {
        List<StationItem> stations = new ArrayList<>();

        // Data mimicking your design screenshots (Brooklyn/NYC area)
        stations.add(new StationItem("S001", "ImPark Underhill Garage", "Brooklyn, 105 Underhill Ave",
                40.6782, -73.9442, true, 4, 150, 4.5, 128));
        stations.add(new StationItem("S002", "99 Prospect Park W", "Brooklyn, 589 Prospect Avenue",
                40.6601, -73.9712, false, 2, 50, 4.0, 55));
        stations.add(new StationItem("S003", "Walgreens - Brooklyn, NY", "Brooklyn, 2901 Atlantic Ave",
                40.6756, -73.9023, true, 3, 250, 4.8, 210));
        stations.add(new StationItem("S004", "Rapidpark 906 Union St", "Brooklyn, 906 Union St",
                40.6715, -73.9789, true, 1, 50, 3.9, 15));
        stations.add(new StationItem("S005", "425 Bond St Garage", "Brooklyn, 425 Bond St",
                40.6865, -73.9922, false, 6, 350, 4.9, 300));
        stations.add(new StationItem("S006", "MTY Parking 735 Kent Ave", "Brooklyn, 735 Kent Ave",
                40.7078, -73.9654, true, 2, 100, 4.2, 70));

        return stations;
    }

    /**
     * Finds a single StationItem based on its unique ID.
     * This implementation fixes the 'cannot find symbol' error in BookingActivity.
     */
    public static StationItem getStationById(String id) {
        if (id == null) return null;

        // Iterate through the mock list and find the matching ID
        for (StationItem station : getMockStations()) {
            if (id.equals(station.getId())) {
                return station;
            }
        }
        return null; // Return null if no station matches the ID
    }
}