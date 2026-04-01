package com.example.chargeeasy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * MapFragment initializes the Google Map and centers on a default
 * Brooklyn location to display station markers.
 * (This is the full, functional version, not the placeholder).
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient; // For the blue "my location" dot

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout that CONTAINS the map container
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (getContext() != null) {
            dbHelper = new DatabaseHelper(getContext());
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }

        // Find the SupportMapFragment we are about to add
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_container);

        // If it's not already there (on first load), create it and add it
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.map_container, mapFragment).commit();
        }

        // Register the callback
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        // --- Center the camera on Brooklyn (as requested) ---
        LatLng defaultLocation = new LatLng(40.6782, -73.9442);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f));

        // Try to enable the user's blue dot (requires permission)
        enableMyLocation();

        // Add the station markers
        addMarkersToMap();
    }

    private void enableMyLocation() {
        if (getContext() == null || getActivity() == null) return;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request permission just for the blue dot
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Permission is granted, enable the blue dot
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, try to enable the blue dot again
                enableMyLocation();
            } else {
                // Permission denied, do nothing (map stays centered on Brooklyn)
                Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Adds station markers to the map.
     * This logic checks the DB for availability to set marker colors.
     */
    private void addMarkersToMap() {
        if (mMap == null || dbHelper == null) return;

        mMap.clear(); // Clear old markers
        List<StationItem> stations = StationDataSource.getMockStations();

        // Get current time in milliseconds to check availability *NOW*
        long nowMs = System.currentTimeMillis();
        long nowEndMs = nowMs + 60000; // Check 1 minute slot starting now

        for (StationItem station : stations) {
            int totalChargers = station.getNumChargers();
            int currentBookings = dbHelper.getOverlappingBookingCount(station.getId(), nowMs, nowEndMs);
            boolean isAvailable = currentBookings < totalChargers;

            LatLng stationLocation = new LatLng(station.getLatitude(), station.getLongitude());

            float markerColor = isAvailable ?
                    BitmapDescriptorFactory.HUE_GREEN :
                    BitmapDescriptorFactory.HUE_RED;

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(stationLocation)
                    .title(station.getName())
                    .snippet(station.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor));

            Marker marker = mMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(station.getId());
            }
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String stationId = (String) marker.getTag();

        if (stationId != null) {
            // Launches the StationDetailsFragment bottom sheet
            StationDetailsFragment dialog = StationDetailsFragment.newInstance(stationId);
            dialog.show(getChildFragmentManager(), "StationDetailsModal");
        }
        return true; // Consume the event
    }
}