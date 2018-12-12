package com.example.utente10.galileo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.utente10.galileo.MapsActivity;

public class RestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (MapsActivity.serviceEnabled){
            context.startService(new Intent(context, TrackerService.class));
        }
    }
}
