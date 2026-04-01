package com.example.chargeeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity serves as the first screen of the onboarding/intro flow.
 * It also acts as a router to check for an ACTIVE LOGIN SESSION.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SessionManager sessionManager; // Use SessionManager for routing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- 1. INITIAL LOGIN CHECK (ROUTER LOGIC) ---
        sessionManager = new SessionManager(this);

        // --- FIX: Check if the user is actively logged in, NOT if a user exists ---
        if (sessionManager.isLoggedIn()) {
            Log.d(TAG, "Active session found. Bypassing intro and navigating to HomeActivity.");

            // Redirect immediately to the main app dashboard
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close this activity
            return; // Stop further execution
        }

        // --- 2. NORMAL INTRO FLOW ---
        // If no active session found, proceed to show the intro screen
        setContentView(R.layout.activity_main);
    }

    /**
     * Handles the logic when the 'Next' button is clicked.
     */
    public void onNextClick(View view) {
        Log.d(TAG, "Next button clicked. Navigating to reservation intro screen.");
        Intent intent = new Intent(MainActivity.this, IntroReservationActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the logic when the 'Skip' button is clicked.
     */
    public void onSkipClick(View view) {
        Log.d(TAG, "Skip button clicked. Navigating to Sign In page.");
        Toast.makeText(this, "Skipping intro. Please sign in.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}