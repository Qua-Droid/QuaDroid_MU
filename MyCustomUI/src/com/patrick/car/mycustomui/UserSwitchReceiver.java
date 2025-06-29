package com.patrick.car.mycustomui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UserSwitchReceiver extends BroadcastReceiver {
    private static final String TAG = "MyPassengerUserPicker";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received " + intent.getAction() + " in UserSwitchReceiver, userId=" + intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1));
        try {
            Intent activityIntent = new Intent(context, MyPassengerUserPicker.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(activityIntent);
            Log.d(TAG, "Sent intent to finish MyPassengerUserPicker");
        } catch (Exception e) {
            Log.e(TAG, "Failed to finish MyPassengerUserPicker", e);
        }
    }
}