package com.example.chargeeasy;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener;

import com.example.chargeeasy.ProfileFragment;
import com.example.chargeeasy.SupportFragment;
import com.example.chargeeasy.BookingFragment;
import com.example.chargeeasy.HomeFragment; // <-- REQUIRED IMPORT

/**
 * HomeActivity is the main container for the application.
 * It manages the Bottom Navigation Bar and swaps Fragments.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationView bottomNavigationView;

    // Define fragments using the actual classes we built
    private final Fragment homeFragment = new HomeFragment(); // <-- FINAL UPDATE
    private final Fragment bookingFragment = new BookingFragment();
    private final Fragment supportFragment = new SupportFragment();
    private final Fragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the initial fragment (Home/Map View)
        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }

        bottomNavigationView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    loadFragment(homeFragment);
                    return true;
                } else if (itemId == R.id.nav_booking) {
                    loadFragment(bookingFragment);
                    return true;
                } else if (itemId == R.id.nav_support) {
                    loadFragment(supportFragment);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(profileFragment);
                    return true;
                }
                return false;
            }
        });

        checkLocationPermission();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void checkLocationPermission() {
        Toast.makeText(this, "Checking Location Permission (Simulated)", Toast.LENGTH_SHORT).show();
    }

    // NOTE: Inner class for generic placeholders is no longer needed since we use concrete fragments.
    // However, if you have other placeholders, keep the inner class definition if needed elsewhere.
}