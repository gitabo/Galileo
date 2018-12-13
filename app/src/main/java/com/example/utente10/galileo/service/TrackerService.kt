package com.example.utente10.galileo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class TrackerService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent(this, RestarterBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
