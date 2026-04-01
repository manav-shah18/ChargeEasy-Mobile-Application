package com.example.chargeeasy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

/**
 * SuccessDialogFragment displays the "Verification Successful!" modal
 * and intelligently routes the user to Home (if existing) or Profile (if new).
 */
public class SuccessDialogFragment extends DialogFragment {

    private static final String TAG = "SuccessDialog";
    private static final long REDIRECT_DELAY_MS = 2000; // 2-second delay
    private String contactNumber;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    // Factory method to pass the phone number securely
    public static SuccessDialogFragment newInstance(String phoneNumber) {
        SuccessDialogFragment fragment = new SuccessDialogFragment();
        Bundle args = new Bundle();
        args.putString("CONTACT_NUMBER", phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize helpers
        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        if (getArguments() != null) {
            contactNumber = getArguments().getString("CONTACT_NUMBER");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // --- CRITICAL FIX: Inflate the correct layout file ---
        // Your file is named 'activity_success_dialog_fragment.xml'
        View view = inflater.inflate(R.layout.activity_success_dialog_fragment, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().setCanceledOnTouchOutside(false);
            setCancelable(false);
        }

        // Use a Handler to delay the navigation
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextStep, REDIRECT_DELAY_MS);

        return view;
    }

    /**
     * FIX: Checks if the user exists. If yes, go to Home. If no, go to Complete Profile.
     */
    private void navigateToNextStep() {
        FragmentActivity activity = getActivity();
        if (activity == null || contactNumber == null) {
            dismiss();
            return;
        }

        Cursor cursor = null;
        Intent intent;

        try {
            cursor = dbHelper.getUserByPhone(contactNumber);

            if (cursor != null && cursor.getCount() > 0) {
                // --- USER EXISTS (LOGIN) ---
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));

                // 1. Create a new session for this user
                sessionManager.createLoginSession(userId);
                Log.d(TAG, "Existing user logged in (ID: " + userId + "). Navigating to HomeActivity.");

                // 2. Set navigation target to HomeActivity
                intent = new Intent(activity, HomeActivity.class);

            } else {
                // --- NEW USER (SIGN UP) ---
                Log.d(TAG, "New user. Navigating to CompleteProfileActivity.");

                // 1. Set navigation target to CompleteProfileActivity
                intent = new Intent(activity, CompleteProfileActivity.class);

                // 2. Pass the phone number so the profile page can display it
                intent.putExtra("CONTACT_NUMBER", contactNumber);
            }

            // Clear the activity back stack and start the new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error checking user existence in DB", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        dismiss(); // Close the success modal
    }
}