package com.example.utente10.galileo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class TrackerService : Service() {

    //Setting automatic restart after App is manually closed
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackerService", "ON")
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
