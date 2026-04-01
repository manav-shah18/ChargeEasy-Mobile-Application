package com.example.chargeeasy;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
// import android.widget.Button; // <-- 1. REMOVED Button import
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = "OtpVerificationActivity";
    private static final int OTP_LENGTH = 4;
    private static final List<String> VALID_OTPS = Arrays.asList("1234", "5678", "9999");

    private TextView tvPhoneNumberDisplay, tvResendTimer, tvResendLink;
    private EditText[] otpFields = new EditText[OTP_LENGTH];
    // private Button btnVerify; // <-- 2. REMOVED Button declaration
    private CountDownTimer resendTimer;
    private String sentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // View bindings
        tvPhoneNumberDisplay = findViewById(R.id.tv_phone_number_display);
        tvResendTimer = findViewById(R.id.tv_resend_timer);
        tvResendLink = findViewById(R.id.tv_resend_link);
        // btnVerify = findViewById(R.id.btn_verify); // <-- 3. REMOVED findViewById

        otpFields[0] = findViewById(R.id.et_otp1);
        otpFields[1] = findViewById(R.id.et_otp2);
        otpFields[2] = findViewById(R.id.et_otp3);
        otpFields[3] = findViewById(R.id.et_otp4);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // --- 4. REMOVED setOnClickListener for the button ---

        // Set the resend listener
        tvResendLink.setOnClickListener(v -> onResendCodeClick(v));

        sentPhoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        if (sentPhoneNumber != null) {
            tvPhoneNumberDisplay.setText("OTP sent to ••••" + sentPhoneNumber.substring(sentPhoneNumber.length() - 4));
        }

        setupOtpInputs();
        startResendTimer();
    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (index < otpFields.length - 1) {
                            // Move focus to the next EditText field
                            otpFields[index + 1].requestFocus();
                        } else {
                            // --- 5. This is now the ONLY way to verify ---
                            // Last digit entered, automatically call verify
                            onVerifyClick(otpFields[index]); // Pass the view
                        }
                    }
                }
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (otpFields[index].getText().length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }
                return false;
            });
        }

        // Request focus for the first box
        otpFields[0].requestFocus();
    }

    // This method is now only called by the TextWatcher
    public void onVerifyClick(View view) {
        StringBuilder otp = new StringBuilder();
        for (EditText field : otpFields) {
            otp.append(field.getText().toString());
        }

        String enteredOtp = otp.toString();

        if (enteredOtp.length() != OTP_LENGTH) {
            // This check is still good as a safeguard
            Toast.makeText(this, "Enter the full 4-digit code.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (VALID_OTPS.contains(enteredOtp)) {
            Toast.makeText(this, "Verification successful!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "OTP verified: " + enteredOtp);

            SuccessDialogFragment dialog = SuccessDialogFragment.newInstance(sentPhoneNumber);
            dialog.show(getSupportFragmentManager(), "SuccessDialog");
        } else {
            Toast.makeText(this, "Invalid code. Try 1234, 5678, or 9999.", Toast.LENGTH_LONG).show();
        }
    }

    public void onResendCodeClick(View view) {
        Toast.makeText(this, "New OTP requested! Try 1234, 5678, or 9999.", Toast.LENGTH_SHORT).show();
        for (EditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
        startResendTimer();
    }

    private void startResendTimer() {
        tvResendTimer.setVisibility(View.VISIBLE);
        tvResendLink.setVisibility(View.GONE);

        resendTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvResendTimer.setText("Resend in " + (millisUntilFinished / 1000) + "s");
            }

            public void onFinish() {
                tvResendTimer.setVisibility(View.GONE);
                tvResendLink.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) resendTimer.cancel();
    }
}