package com.example.chargeeasy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * ProfileFragment displays the user's profile information
 * and handles the session logout.
 */
public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail, tvUserContact, tvSettings, tvLogout;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager; // <-- Required for logout
    private long currentUserId = -1;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null) return;
        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext()); // <-- Initialize SessionManager

        currentUserId = sessionManager.getActiveUserId();

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        tvUserContact = view.findViewById(R.id.tv_user_contact);
        tvSettings = view.findViewById(R.id.tv_settings);
        tvLogout = view.findViewById(R.id.tv_logout);

        loadProfileData();

        tvSettings.setOnClickListener(v -> onSettingsClicked());
        tvLogout.setOnClickListener(v -> onLogoutClicked());
    }

    private void loadProfileData() {
        if (dbHelper == null || currentUserId == -1) {
            tvUserName.setText("Error: Not logged in.");
            return;
        }

        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserProfileById(currentUserId);

            if (cursor != null && cursor.getCount() > 0) {
                int nameCol = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FULL_NAME);
                int emailCol = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL);
                int phoneCol = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PHONE_NUMBER);

                tvUserName.setText(cursor.getString(nameCol));
                tvUserEmail.setText(cursor.getString(emailCol));
                tvUserContact.setText(cursor.getString(phoneCol));
            } else {
                Log.w("ProfileFragment", "No user profile found for ID: " + currentUserId);
            }
        } catch (Exception e) {
            Log.e("ProfileFragment", "Error loading profile data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void onSettingsClicked() {
        Toast.makeText(getContext(), "Opening Settings...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the logout action by clearing the session data.
     */
    private void onLogoutClicked() {
        // --- CRITICAL FIX: Clear the active session ---
        sessionManager.logoutUser();

        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_LONG).show();

        // Navigate back to the start (SignInActivity)
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}