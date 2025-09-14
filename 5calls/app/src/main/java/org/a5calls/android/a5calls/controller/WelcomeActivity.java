package org.a5calls.android.a5calls.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.a5calls.android.a5calls.FiveCallsApplication;
import org.a5calls.android.a5calls.R;
import org.a5calls.android.a5calls.model.AccountManager;

/**
 * Single welcome screen that matches the iOS design
 * Replaces the multi-screen TutorialActivity
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    private final AccountManager accountManager = AccountManager.Instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Hide the action bar for full-screen experience
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Track analytics
        FiveCallsApplication.analyticsManager().trackPageview("/welcome", this);

        // Set up the "Set Location" button
        findViewById(R.id.set_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationBottomSheet();
            }
        });

        // Mark tutorial as seen
        accountManager.setTutorialSeen(this, true);
    }

    private void showLocationBottomSheet() {
        // Show the location bottom sheet (same as used in MainActivity)
        LocationBottomSheetFragment locationBottomSheet = LocationBottomSheetFragment.newInstance();
        locationBottomSheet.setLocationSetListener(location -> {
            // When location is set, proceed to main activity
            proceedToMainActivity();
        });
        locationBottomSheet.show(getSupportFragmentManager(), "LocationBottomSheet");
    }

    private void proceedToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close welcome activity
    }

    @Override
    public void onBackPressed() {
        // Prevent back navigation from welcome screen
        // User must set location to proceed
        // Do nothing - user must complete onboarding
    }
}