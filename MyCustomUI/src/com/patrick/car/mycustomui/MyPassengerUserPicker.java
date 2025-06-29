package com.patrick.car.mycustomui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPassengerUserPicker extends Activity {
    private static final String TAG = "MyPassengerUserPicker";
    private BroadcastReceiver userSwitchReceiver;
    private BroadcastReceiver bootReceiver;
    private int secondaryDisplayId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called, launching UI");

        // Log all displays
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();
        Log.d(TAG, "Available displays: " + displays.length);
        for (Display display : displays) {
            Log.d(TAG, "Display ID: " + display.getDisplayId() + ", Name: " + display.getName());
            if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                secondaryDisplayId = display.getDisplayId();
                Log.d(TAG, "Selected secondary display: " + secondaryDisplayId);
            }
        }

        if (secondaryDisplayId == -1) {
            Log.e(TAG, "No secondary display detected");
            Toast.makeText(this, "No secondary display detected", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (getDisplay().getDisplayId() != secondaryDisplayId) {
            Log.d(TAG, "Moving to secondary display: " + secondaryDisplayId);
            launchOnSecondaryDisplay();
            return;
        }

        setContentView(R.layout.user_picker_layout);
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.your_logo);
        TextView prompt = findViewById(R.id.user_prompt);
        prompt.setText("Waiting for User Selection");

        // Add test button for debugging
        Button testButton = findViewById(R.id.test_button);
        if (testButton != null) {
            testButton.setOnClickListener(v -> {
                Log.d(TAG, "Test button clicked, finishing activity");
                finish();
            });
        }

        // Register USER_SWITCHED receiver
        userSwitchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received USER_SWITCHED, finishing activity");
                finish();
            }
        };
        IntentFilter userFilter = new IntentFilter("android.intent.action.USER_SWITCHED");
        registerReceiver(userSwitchReceiver, userFilter);
        Log.d(TAG, "Registered USER_SWITCHED receiver");

        // Register BOOT_COMPLETED receiver
        bootReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received BOOT_COMPLETED, launching on secondary display");
                if (secondaryDisplayId != -1 && getDisplay().getDisplayId() != secondaryDisplayId) {
                    launchOnSecondaryDisplay();
                }
            }
        };
        IntentFilter bootFilter = new IntentFilter("android.intent.action.BOOT_COMPLETED");
        registerReceiver(bootReceiver, bootFilter);
        Log.d(TAG, "Registered BOOT_COMPLETED receiver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (userSwitchReceiver != null) {
            unregisterReceiver(userSwitchReceiver);
            Log.d(TAG, "Unregistered userSwitchReceiver");
        }
        if (bootReceiver != null) {
            unregisterReceiver(bootReceiver);
            Log.d(TAG, "Unregistered bootReceiver");
        }
    }

    private void launchOnSecondaryDisplay() {
        try {
            Intent intent = new Intent(this, MyPassengerUserPicker.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeBasic();
            options.setLaunchDisplayId(secondaryDisplayId);
            Log.d(TAG, "Launching activity on display: " + secondaryDisplayId);
            startActivity(intent, options.toBundle());
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch on display " + secondaryDisplayId, e);
            Toast.makeText(this, "Failed to launch on secondary display: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}