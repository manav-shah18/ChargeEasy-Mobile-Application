package com.example.chargeeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneAuthActivity extends AppCompatActivity {

    private static final String TAG = "PhoneAuthActivity";
    private EditText etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        etPhoneNumber = findViewById(R.id.et_phone_number);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    public void onSignInClick(View view) {
        String phone = etPhoneNumber.getText().toString().trim().replaceAll("[^0-9]", "");

        if (phone.length() != 10) {
            Toast.makeText(this, "Enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Valid phone entered: " + phone);

        Intent intent = new Intent(PhoneAuthActivity.this, OtpVerificationActivity.class);
        intent.putExtra("PHONE_NUMBER", phone);
        startActivity(intent);
    }

    public void onUseEmailSignInClick(View view) {
        Toast.makeText(this, "Email Sign-In coming soon.", Toast.LENGTH_SHORT).show();
    }

    public void onSocialSignInClick(View view) {
        Toast.makeText(this, "Social Sign-In coming soon.", Toast.LENGTH_SHORT).show();
    }
}
