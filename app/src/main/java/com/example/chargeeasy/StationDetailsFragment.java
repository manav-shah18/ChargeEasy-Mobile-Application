package com.example.chargeeasy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

/**
 * StationDetailsFragment displays detailed information for a selected charging station
 * in a persistent Bottom Sheet Modal (Screens 23/24).
 */
public class StationDetailsFragment extends BottomSheetDialogFragment {

    private static final String TAG = "StationDetailsFragment";
    private static final String ARG_STATION_ID = "station_id";
    private StationItem selectedStation;

    // Factory method to safely pass the Station ID to the fragment
    public static StationDetailsFragment newInstance(String stationId) {
        StationDetailsFragment fragment = new StationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATION_ID, stationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String stationId = getArguments().getString(ARG_STATION_ID);
            for (StationItem item : StationDataSource.getMockStations()) {
                if (item.getId().equals(stationId)) {
                    selectedStation = item;
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (selectedStation == null) {
            Toast.makeText(getContext(), "Error: Station details not found.", Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }

        // Initialize Views
        TextView tvName = view.findViewById(R.id.tv_station_name_detail);
        TextView tvAddress = view.findViewById(R.id.tv_station_address_detail);
        TextView tvRating = view.findViewById(R.id.tv_rating_detail);
        TextView tvStatus = view.findViewById(R.id.tv_status_detail);
        TextView tvChargersAvailable = view.findViewById(R.id.tv_chargers_available);
        TextView tvChargerDetails = view.findViewById(R.id.tv_charger_details);

        Button btnView = view.findViewById(R.id.btn_view_on_map);
        Button btnBook = view.findViewById(R.id.btn_book_now);

        // Populate Views with data
        tvName.setText(selectedStation.getName());
        tvAddress.setText(selectedStation.getAddress());
        tvRating.setText(String.format(Locale.US, "%.1f (%d reviews)", selectedStation.getRating(), selectedStation.getReviewCount()));

        // Status Colors
        int statusColor = selectedStation.isAvailable() ? ContextCompat.getColor(requireContext(), R.color.green_primary) : ContextCompat.getColor(requireContext(), R.color.red_unavailable);
        tvStatus.setText(selectedStation.isAvailable() ? "AVAILABLE" : "UNAVAILABLE");
        tvStatus.setTextColor(statusColor);

        String chargerCount = selectedStation.isAvailable() ? String.valueOf(selectedStation.getNumChargers()) + " chargers" : "Fully Booked";
        tvChargersAvailable.setText(chargerCount);

        tvChargerDetails.setText(String.format(Locale.US,
                "Max Power: %.0f kW | Connectors: CCS, CHAdeMO | ID: %s",
                selectedStation.getMaxPowerKw(), selectedStation.getId()));

        // --- Action Button Handlers (FIXED) ---
        btnView.setOnClickListener(v -> onGetDirectionClicked());
        btnBook.setOnClickListener(v -> onBookNowClicked()); // Now calls the new navigation logic
    }

    private void onGetDirectionClicked() {
        if (selectedStation == null) return;

        String uri = String.format(Locale.US, "google.navigation:q=%f,%f",
                selectedStation.getLatitude(), selectedStation.getLongitude());

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "Google Maps app not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onBookNowClicked() {
        if (selectedStation == null) return;

        // --- CRITICAL NAVIGATION FIX: Launch Booking Activity ---
        Intent intent = new Intent(getActivity(), BookingActivity.class);
        // Pass the station ID so BookingActivity knows what station to reserve
        intent.putExtra(BookingActivity.EXTRA_STATION_ID, selectedStation.getId());
        startActivity(intent);

        dismiss(); // Close the bottom sheet after launching the new activity
    }
}
