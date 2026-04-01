package com.example.chargeeasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * StationListFragment displays charging stations in a vertical scrolling list format.
 * It uses the StationListAdapter and StationDataSource.
 */
public class StationListFragment extends Fragment {

    private RecyclerView recyclerView;
    private StationListAdapter adapter; // <-- Uses the correct adapter
    private List<StationItem> stationList;
    private DatabaseHelper dbHelper; // For checking availability

    public StationListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_stations);
        dbHelper = new DatabaseHelper(getContext());

        // 1. Get Mock Data
        // We get the *base* data from the mock source
        stationList = StationDataSource.getMockStations();

        // 2. Update availability status based on DB (same logic as MapFragment)
        updateStationAvailability();

        // 3. Setup RecyclerView
        adapter = new StationListAdapter(stationList); // <-- Use the corrected adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list availability every time the user toggles to it
        updateStationAvailability();
        if (adapter != null) {
            adapter.updateList(stationList);
        }
    }

    /**
     * Updates the 'isAvailable' flag in the list based on current DB bookings.
     */
    private void updateStationAvailability() {
        if (dbHelper == null) return;

        // Get current time
        long nowMs = System.currentTimeMillis();
        long nowEndMs = nowMs + 60000; // Check 1 minute slot from now

        for (StationItem station : stationList) {
            int totalChargers = station.getNumChargers();
            int currentBookings = dbHelper.getOverlappingBookingCount(station.getId(), nowMs, nowEndMs);

            // This is the dynamic link to the database
            station.setAvailable(currentBookings < totalChargers);
        }
    }
}