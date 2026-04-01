package com.example.chargeeasy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_STATION_ID = "station_id";
    private static final String TAG = "BookingActivity";

    private EditText etDate, etTime;
    private TextInputLayout tilDate, tilTime;
    private TextView tvStationName, tvEstimatedCost; // Declared
    private RecyclerView rvSlots;
    private SlotAdapter slotAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private long currentUserId = -1;
    private StationItem currentStation;
    private Calendar selectedStartCalendar; // Use Calendar object

    private int selectedDurationMinutes = 60;
    private final double COST_PER_MINUTE = 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        selectedStartCalendar = Calendar.getInstance();

        currentUserId = sessionManager.getActiveUserId();
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: You are not logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- 1. INITIALIZATION (CRITICAL FIX) ---
        // Find all views *before* using them
        tilDate = findViewById(R.id.til_booking_date);
        etDate = findViewById(R.id.et_booking_date);
        tilTime = findViewById(R.id.til_booking_time);
        etTime = findViewById(R.id.et_booking_time);
        tvStationName = findViewById(R.id.tv_station_name_booking);
        rvSlots = findViewById(R.id.rv_time_slots);

        // --- THIS LINE WAS MISSING ---
        tvEstimatedCost = findViewById(R.id.tv_estimated_cost);
        // -------------------------------

        // 2. Load data and setup UI
        loadStationData();
        setupDatePicker();
        setupTimePicker();
        setupTimeSlots();

        // 3. Set the default selection and update cost *after* all views are initialized
        onSlotSelected(slotAdapter.getDefaultSlot());

        findViewById(R.id.btn_confirm_booking).setOnClickListener(v -> onConfirmBookingClick());
    }

    private void loadStationData() {
        String stationId = getIntent().getStringExtra(EXTRA_STATION_ID);
        currentStation = StationDataSource.getStationById(stationId);
        if (currentStation != null) {
            tvStationName.setText(currentStation.getName());
        } else {
            tvStationName.setText("Station Not Found");
        }
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> showDatePickerDialog());
        tilDate.setEndIconOnClickListener(v -> showDatePickerDialog());
        etDate.setFocusable(false);
        etDate.setKeyListener(null);
        etDate.setText(TimeUtils.formatDate(selectedStartCalendar));
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> showTimePickerDialog());
        tilTime.setEndIconOnClickListener(v -> showTimePickerDialog());
        etTime.setFocusable(false);
        etTime.setKeyListener(null);
        etTime.setText(TimeUtils.formatTime(selectedStartCalendar));
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    selectedStartCalendar.set(Calendar.YEAR, year);
                    selectedStartCalendar.set(Calendar.MONTH, month);
                    selectedStartCalendar.set(Calendar.DAY_OF_MONTH, day);
                    etDate.setText(TimeUtils.formatDate(selectedStartCalendar));
                },
                selectedStartCalendar.get(Calendar.YEAR),
                selectedStartCalendar.get(Calendar.MONTH),
                selectedStartCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    selectedStartCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedStartCalendar.set(Calendar.MINUTE, minute);
                    etTime.setText(TimeUtils.formatTime(selectedStartCalendar));
                },
                selectedStartCalendar.get(Calendar.HOUR_OF_DAY),
                selectedStartCalendar.get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    private void setupTimeSlots() {
        List<String> mockDurations = new ArrayList<>();
        mockDurations.add("30 Min");
        mockDurations.add("1 Hour");
        mockDurations.add("1.5 Hour");
        mockDurations.add("2 Hour");

        slotAdapter = new SlotAdapter(mockDurations, this::onSlotSelected);
        rvSlots.setLayoutManager(new GridLayoutManager(this, 3));
        rvSlots.setAdapter(slotAdapter);
    }

    private void onSlotSelected(String slot) {
        try {
            if (slot.contains("Min")) {
                selectedDurationMinutes = Integer.parseInt(slot.replace(" Min", "").trim());
            } else if (slot.contains("Hour")) {
                String hourStr = slot.replace(" Hour", "").trim();
                double hours = Double.parseDouble(hourStr);
                selectedDurationMinutes = (int) (hours * 60);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing duration: " + slot);
            selectedDurationMinutes = 60; // Fallback
        }
        updateCost();
    }

    private void updateCost() {
        double cost = selectedDurationMinutes * COST_PER_MINUTE;
        // This line is now safe
        tvEstimatedCost.setText(String.format(Locale.US, "$%.2f", cost));
    }

    private void onConfirmBookingClick() {
        if (currentStation == null || currentUserId == -1) {
            Toast.makeText(this, "Cannot book: station data invalid.", Toast.LENGTH_SHORT).show();
            return;
        }

        long startTimeMs = selectedStartCalendar.getTimeInMillis();
        long endTimeMs = startTimeMs + (selectedDurationMinutes * 60 * 1000);

        int totalChargers = currentStation.getNumChargers();
        int overlappingBookings = dbHelper.getOverlappingBookingCount(currentStation.getId(), startTimeMs, endTimeMs);

        if (overlappingBookings >= totalChargers) {
            Toast.makeText(this, "Booking Failed: All chargers are busy during this time range.", Toast.LENGTH_LONG).show();
            return;
        }

        BookingItem newBooking = new BookingItem(
                "B" + System.currentTimeMillis(),
                currentStation.getId(),
                currentStation.getName(),
                currentStation.getAddress(),
                startTimeMs,
                endTimeMs,
                selectedDurationMinutes,
                selectedDurationMinutes * COST_PER_MINUTE,
                false
        );

        boolean success = dbHelper.insertBooking(newBooking, currentUserId);

        if (success) {
            Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: Could not save booking.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- (Helper methods unchanged) ---
    private String formatDate(Calendar calendar) {
        return String.format(Locale.US, "%02d/%02d/%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
    }

    private String formatTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) hour = 12;
        return String.format(Locale.US, "%d:%02d %s",
                hour,
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
    }
}