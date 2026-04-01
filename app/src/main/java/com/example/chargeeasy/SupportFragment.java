package com.example.chargeeasy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * SupportFragment provides users with self-help options (FAQ) and direct contact methods (Call, Email).
 */
public class SupportFragment extends Fragment {

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup click listeners for the main action buttons/links
        view.findViewById(R.id.tv_faq).setOnClickListener(v -> onFaqClicked());
        view.findViewById(R.id.tv_call_support).setOnClickListener(v -> onCallSupportClicked());
        view.findViewById(R.id.tv_email_support).setOnClickListener(v -> onEmailSupportClicked());
    }

    /**
     * Placeholder action for viewing FAQs.
     */
    private void onFaqClicked() {
        Toast.makeText(getContext(), "Navigating to FAQ / Help Center...", Toast.LENGTH_SHORT).show();
        // TODO: Launch a new Activity or WebView for FAQ content
    }

    /**
     * Initiates a phone call to the support number.
     */
    private void onCallSupportClicked() {
        // Use a placeholder support number
        String phoneNumber = "tel:08001234567";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneNumber));
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Error: Cannot find an application to place a call.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initiates an email to the support address.
     */
    private void onEmailSupportClicked() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@chargeeasy.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "ChargeEasy Support Request");

        try {
            startActivity(Intent.createChooser(intent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Error: No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}