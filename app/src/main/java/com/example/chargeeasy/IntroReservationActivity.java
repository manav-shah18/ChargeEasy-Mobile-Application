package com.example.chargeeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Second onboarding screen focused on the reservation and check-in feature.
 * The 'Next' button now navigates to the LoginActivity.
 */
public class IntroReservationActivity extends AppCompatActivity {

    private static final String TAG = "IntroReservationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout defined in activity_intro_reservation.xml
        setContentView(R.layout.activity_intro_reservation);
    }

    /**
     * Handles the logic for the 'Next' button (referenced in XML: android:onClick="onNextClick").
     * Navigates to the LoginActivity, completing the intro flow.
     */
    public void onNextClick(View view) {
        Log.d(TAG, "Next button clicked. Navigating to Login screen.");

        // Intent to launch the LoginActivity
        Intent intent = new Intent(IntroReservationActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity so the user cannot go back to the intro
    }

    /**
     * Handles the logic for the 'Skip' button (referenced in XML: android:onClick="onSkipClick").
     * This also bypasses the rest of the intro flow and goes straight to Login.
     */
    public void onSkipClick(View view) {
        Log.d(TAG, "Skip button clicked from Reservation screen. Navigating to Login.");
        Toast.makeText(this, "Skipping intro. Please log in.", Toast.LENGTH_SHORT).show();

        // Intent to launch the LoginActivity
        Intent intent = new Intent(IntroReservationActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
}
