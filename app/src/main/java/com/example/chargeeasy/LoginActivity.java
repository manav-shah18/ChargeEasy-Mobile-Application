package com.example.chargeeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity is the entry point for authentication, allowing users to choose
 * their preferred sign-in method (Social or Phone Number).
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout defined in activity_login.xml
        setContentView(R.layout.activity_login);
    }

    /**
     * Handles sign-in attempts using Google.
     */
    public void onGoogleSigninClick(View view) {
        Log.d(TAG, "Google Sign-In clicked.");
        Toast.makeText(this, "Initiating Google Sign-In...", Toast.LENGTH_SHORT).show();
        // TODO: Implement Google Sign-In authentication logic (e.g., Firebase Auth)
    }

    /**
     * Handles sign-in attempts using Facebook.
     */
    public void onFacebookSigninClick(View view) {
        Log.d(TAG, "Facebook Sign-In clicked.");
        Toast.makeText(this, "Initiating Facebook Sign-In...", Toast.LENGTH_SHORT).show();
        // TODO: Implement Facebook Sign-In authentication logic
    }

    /**
     * Handles sign-in attempts using Apple.
     */
    public void onAppleSigninClick(View view) {
        Log.d(TAG, "Apple Sign-In clicked.");
        Toast.makeText(this, "Initiating Apple Sign-In...", Toast.LENGTH_SHORT).show();
        // TODO: Implement Apple Sign-In authentication logic
    }

    /**
     * HANDLES 'SIGN IN WITH PHONE NUMBER' CLICK
     * Navigates to the PhoneAuthActivity to begin the verification process.
     */
    public void onPhoneSigninClick(View view) {
        Log.d(TAG, "Phone Sign-In clicked. Navigating to PhoneAuthActivity.");
        // Navigate to the Phone Number input screen (Step 1 of the new flow)
        Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the 'Sign up' link click at the bottom.
     */
    public void onSignUpClick(View view) {
        Log.d(TAG, "Sign Up link clicked.");
        Toast.makeText(this, "Navigating to Registration screen...", Toast.LENGTH_SHORT).show();
        // TODO: Implement Intent to your registration/sign-up activity
    }

    /**
     * Placeholder method to navigate to the main app dashboard after successful login.
     */
    private void navigateToMainApp() {
        Toast.makeText(this, "Login successful! Entering ChargeEasy app.", Toast.LENGTH_LONG).show();
        // TODO: Replace PlaceholderActivity.class with your main dashboard/map activity
        // Intent intent = new Intent(LoginActivity.this, MainMapActivity.class);
        // startActivity(intent);
        finish();
    }
}