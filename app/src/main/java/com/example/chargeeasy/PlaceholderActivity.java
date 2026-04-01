package com.example.chargeeasy;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * PlaceholderActivity serves as the main application dashboard/map screen
 * after a successful login.
 */
public class PlaceholderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity needs a simple layout to display success

        // Minimal layout to confirm success. Create activity_placeholder.xml for proper layout.
        TextView tv = new TextView(this);
        tv.setTextSize(30);
        tv.setText("Welcome to ChargeEasy! (Main App Dashboard)");
        setContentView(tv);

        // Note: For a proper implementation, you should create a minimal layout XML
        // named 'activity_placeholder.xml' and use setContentView(R.layout.activity_placeholder);
    }
}