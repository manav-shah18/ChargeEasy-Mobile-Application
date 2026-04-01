package com.example.chargeeasy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * CompleteProfileActivity collects user details, saves them to SQLite,
 * and starts a new session using the SessionManager.
 */
public class CompleteProfileActivity extends AppCompatActivity {

    private static final String TAG = "CompleteProfileActivity";
    private EditText etFullName, etEmailId, etDateOfBirth;
    private TextView tvContactNumber;
    private Button btnContinue;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager; // <-- NEW

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@(gmail|outlook)\\.com$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        // Initialize Helpers
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this); // <-- NEW

        etFullName = findViewById(R.id.et_full_name);
        etEmailId = findViewById(R.id.et_email_id);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        tvContactNumber = findViewById(R.id.tv_contact_number);
        btnContinue = findViewById(R.id.btn_continue_profile);

        String contactNumber = getIntent().getStringExtra("CONTACT_NUMBER");
        if (contactNumber != null) {
            tvContactNumber.setText(contactNumber);
        } else {
            tvContactNumber.setText("N/A");
        }

        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        etDateOfBirth.setFocusable(false);
        etDateOfBirth.setKeyListener(null);
        btnContinue.setOnClickListener(v -> onContinueClick());
    }

    private void showDatePicker() {
        final Calendar eighteenYearsAgo = Calendar.getInstance();
        eighteenYearsAgo.add(Calendar.YEAR, -18);

        int year = eighteenYearsAgo.get(Calendar.YEAR);
        int month = eighteenYearsAgo.get(Calendar.MONTH);
        int day = eighteenYearsAgo.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.US, "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etDateOfBirth.setText(date);
                }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(eighteenYearsAgo.getTimeInMillis());
        datePickerDialog.show();
    }

    private boolean isUserEighteenOrOlder(String dobString) {
        try {
            Date dob = dateFormatter.parse(dobString);
            Calendar dobCalendar = Calendar.getInstance();
            if (dob != null) dobCalendar.setTime(dob);
            Calendar eighteenYearsAgo = Calendar.getInstance();
            eighteenYearsAgo.add(Calendar.YEAR, -18);
            return dobCalendar.before(eighteenYearsAgo) || dobCalendar.equals(eighteenYearsAgo);
        } catch (ParseException e) {
            return false;
        }
    }

    private void onContinueClick() {
        String fullName = etFullName.getText().toString().trim();
        String emailId = etEmailId.getText().toString().trim();
        String dob = etDateOfBirth.getText().toString().trim();
        String contactNumber = tvContactNumber.getText().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(emailId) || TextUtils.isEmpty(dob)) {
            Toast.makeText(this, "Please fill in all profile details.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!EMAIL_PATTERN.matcher(emailId).matches()) {
            Toast.makeText(this, "Email must be a valid @gmail.com or @outlook.com address.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isUserEighteenOrOlder(dob)) {
            Toast.makeText(this, "You must be at least 18 years old.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. SAVE DATA TO SQLITE
        long newUserId = dbHelper.addUser(fullName, emailId, dob, contactNumber);

        if (newUserId != -1) {
            // 4. CREATE A LOGIN SESSION
            sessionManager.createLoginSession(newUserId);
            Log.d(TAG, "Profile saved to SQLite, new user ID: " + newUserId + ". Session created.");
            navigateToMainApp();
        } else {
            Toast.makeText(this, "Error saving profile. Phone number might already exist.", Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToMainApp() {
        Toast.makeText(this, "Profile complete. Welcome to ChargeEasy!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}