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

import java.util.ArrayList;
import java.util.List;

/**
 * UpcomingBookingsFragment fetches data from SQLite for the active user.
 * It now uses the dedicated BookingListAdapter.
 */
public class UpcomingBookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingListAdapter adapter; // <-- FIX: Use new adapter
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long currentUserId = -1;

    public UpcomingBookingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_list_container, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) return;
        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());
        currentUserId = sessionManager.getActiveUserId();

        recyclerView = view.findViewById(R.id.recycler_view_booking_sessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookingListAdapter(new ArrayList<>()); // <-- FIX: Use new adapter
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            refreshBookingData();
        }
    }

    public void refreshBookingData() {
        if (dbHelper != null && adapter != null && currentUserId != -1) {
            List<BookingItem> upcomingBookings = dbHelper.getBookings(currentUserId, false);

            // NO CONVERSION NEEDED
            adapter.updateList(upcomingBookings);
        }
    }

    // The convertBookingsToStations method is NO LONGER NEEDED here.
}