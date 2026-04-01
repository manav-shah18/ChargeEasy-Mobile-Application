package com.example.chargeeasy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * HomeFragment acts as the primary content view under the Home navigation tab.
 * It manages the Search Bar, the Map/List toggle, and switches between MapFragment and StationListFragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView tvSearchStations;
    private AppCompatButton btnToggleView;

    // Fragments to manage
    private final Fragment mapFragment = new MapFragment();
    private final Fragment listFragment = new StationListFragment(); // Will be created next

    // State management
    private boolean isMapMode = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvSearchStations = view.findViewById(R.id.tv_search_stations);
        btnToggleView = view.findViewById(R.id.btn_toggle_view);

        // Initial setup: Load the map view
        if (savedInstanceState == null) {
            loadChildFragment(mapFragment);
        }

        btnToggleView.setOnClickListener(v -> toggleView());
        tvSearchStations.setOnClickListener(v -> onSearchClicked());

        return view;
    }

    private void loadChildFragment(Fragment fragment) {
        // Use getChildFragmentManager() to place the Map/List inside this parent fragment
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content_container, fragment);
        ft.commit();
    }

    private void toggleView() {
        isMapMode = !isMapMode;

        if (isMapMode) {
            loadChildFragment(mapFragment);
            btnToggleView.setText("List");
            Log.d(TAG, "Switched to Map View");
        } else {
            loadChildFragment(listFragment);
            btnToggleView.setText("Map");
            Log.d(TAG, "Switched to List View");
        }
    }

    private void onSearchClicked() {
        Toast.makeText(getContext(), "Opening Search Functionality...", Toast.LENGTH_SHORT).show();
        // TODO: Implement navigation to a dedicated search activity or expanded view
    }
}
