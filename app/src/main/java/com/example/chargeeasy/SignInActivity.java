package com.example.chargeeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    // ---------------- SOCIAL SIGN-IN ----------------
    public void onGoogleSigninClick(View view) {
        Log.d(TAG, "Google Sign-In clicked.");
        Toast.makeText(this, "Google Sign-In not implemented yet.", Toast.LENGTH_SHORT).show();
    }

    public void onFacebookSigninClick(View view) {
        Log.d(TAG, "Facebook Sign-In clicked.");
        Toast.makeText(this, "Facebook Sign-In not implemented yet.", Toast.LENGTH_SHORT).show();
    }

    public void onAppleSigninClick(View view) {
        Log.d(TAG, "Apple Sign-In clicked.");
        Toast.makeText(this, "Apple Sign-In not implemented yet.", Toast.LENGTH_SHORT).show();
    }

    // ---------------- PHONE NUMBER SIGN-IN ----------------
    public void onPhoneSigninClick(View view) {
        Log.d(TAG, "Phone Sign-In clicked. Navigating to PhoneAuthActivity.");
        Intent intent = new Intent(SignInActivity.this, PhoneAuthActivity.class);
        startActivity(intent);
    }

    // ---------------- SIGN-UP LINK ----------------
    public void onSignUpClick(View view) {
        Log.d(TAG, "Sign Up link clicked.");
        Toast.makeText(this, "Registration flow coming soon.", Toast.LENGTH_SHORT).show();
    }

    // ---------------- PLACEHOLDER NAVIGATION ----------------
    private void navigateToMainApp() {
        Toast.makeText(this, "Login successful! Redirecting to app...", Toast.LENGTH_SHORT).show();
        // Replace with your main app activity
        // Intent intent = new Intent(this, HomeActivity.class);
        // startActivity(intent);
        finish();
    }
}
