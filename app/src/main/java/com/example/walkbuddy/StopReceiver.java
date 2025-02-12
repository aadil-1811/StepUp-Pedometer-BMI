package com.example.walkbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context.getApplicationContext(), StepsService.class));
        Toast.makeText(context.getApplicationContext(), "Step Tracking stopped", Toast.LENGTH_SHORT).show();
    }
}
