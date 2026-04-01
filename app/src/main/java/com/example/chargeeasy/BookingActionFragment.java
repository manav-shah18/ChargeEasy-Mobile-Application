package com.example.chargeeasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * BookingActionFragment provides options to Cancel or Complete an active booking
 * by updating the SQLite database.
 */
public class BookingActionFragment extends BottomSheetDialogFragment {

    private static final String ARG_BOOKING_ID = "booking_id";
    private DatabaseHelper dbHelper;
    private String currentBookingId;

    public static BookingActionFragment newInstance(String bookingId) {
        BookingActionFragment fragment = new BookingActionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOKING_ID, bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This 'getArguments()' call will work once the XML layout is fixed
        if (getArguments() != null) {
            currentBookingId = getArguments().getString(ARG_BOOKING_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // --- CRITICAL FIX: ---
        // Renamed to match your XML file (fragment_booking_action.xml - no 's')
        return inflater.inflate(R.layout.fragment_booking_actions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        Button btnComplete = view.findViewById(R.id.btn_complete_booking);
        Button btnCancel = view.findViewById(R.id.btn_cancel_booking);
        TextView btnClose = view.findViewById(R.id.btn_close_modal);

        btnComplete.setOnClickListener(v -> handleBookingAction(true));
        btnCancel.setOnClickListener(v -> handleBookingAction(false));
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void handleBookingAction(boolean complete) {
        if (currentBookingId == null) {
            Toast.makeText(getContext(), "Error: Booking ID missing.", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        if (complete) {
            // Logic to mark booking as completed
            dbHelper.updateBookingStatus(currentBookingId, true);
            Toast.makeText(getContext(), "Session completed! Added to history.", Toast.LENGTH_LONG).show();
        } else {
            // Logic to cancel booking
            dbHelper.deleteBooking(currentBookingId);
            Toast.makeText(getContext(), "Booking canceled.", Toast.LENGTH_LONG).show();
        }

        dismiss(); // Close the modal
        // The Upcoming/Completed fragments will auto-refresh via their onResume()
    }
}