package com.example.utente10.galileo.service

import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.util.Log
import com.example.utente10.galileo.MainActivity
import com.example.utente10.galileo.R
import com.example.utente10.galileo.bean.Landmark
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import io.realm.Realm


class TrackerService : Service() {

    private val CHANNEL_ID = "landmarkChannel"
    private var notificationId = 0

    val proximityManager: ProximityManager by lazy {
        ProximityManagerFactory.create(this)
    }

    private val iBeaconListener: IBeaconListener by lazy {
        object : SimpleIBeaconListener() {
            //function called everytime a beacon is reached
            override fun onIBeaconDiscovered(ibeacon: IBeaconDevice, region: IBeaconRegion?) {
                Log.i("Found Beacon:", "label: " + ibeacon.uniqueId)
                val landmark = getLocationFromBeacon(ibeacon.uniqueId)
                if (landmark != null)
                    sendNotification(landmark)
            }
        }
    }

    private fun sendNotification(landmark: Landmark) {

        // Creating an Intent for the activity to start
        //TODO: change the activity to start on notification click
        val intent = Intent(this, MainActivity::class.java)
        // Create the TaskStackBuilder
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        var mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(landmark.name)
                .setContentText(landmark.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, mBuilder.build())
        }
    }

    //creating notification channel for android>=8.0
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.landmark_channel)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //Setting automatic restart after App is manually closed
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackerService", "ON")

        createNotificationChannel()

        proximityManager.setIBeaconListener(iBeaconListener)
        beaconScanActivation()

        return START_STICKY
    }

    override fun onDestroy() {
        proximityManager.stopScanning()
        proximityManager.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun beaconScanActivation() {

        //ENABLING BLUETOOTH
        val bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled) {
            bluetooth.enable()
        }
        proximityManager.connect {
            proximityManager.startScanning()
        }
    }

    private fun getLocationFromBeacon(beacon: String): Landmark? {
        var realm = Realm.getDefaultInstance()
        return realm.where(Landmark::class.java).equalTo("beacon.label", beacon).findFirst()
    }
}
