package com.example.utente10.galileo.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.example.utente10.galileo.*
import com.example.utente10.galileo.backend.ErrorListener
import com.example.utente10.galileo.backend.Response
import com.example.utente10.galileo.backend.ResponseListener
import com.example.utente10.galileo.backend.sendStatistics
import com.example.utente10.galileo.bean.Landmark
import com.example.utente10.galileo.bean.Macroarea
import com.example.utente10.galileo.notification.createNotificationChannel
import com.example.utente10.galileo.notification.sendNotificationForLandmark
import com.example.utente10.galileo.notification.sendNotificationForMacroarea
import com.google.gson.Gson
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener
import io.realm.Realm
import org.jetbrains.anko.doAsync
import kotlin.reflect.KMutableProperty1


class TrackerService : Service() {

    companion object {
        var uniqueTracker: TrackerService? = null
            private set

        var currentArea: Macroarea? = null
            private set
    }

    val proximityManager: ProximityManager by lazy {
        ProximityManagerFactory.create(this)
    }

    val locationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private val iBeaconListener: IBeaconListener by lazy {
        object : SimpleIBeaconListener() {
            //function called everytime a beacon is reached
            override fun onIBeaconDiscovered(ibeacon: IBeaconDevice, region: IBeaconRegion?) {
                Log.i("Found Beacon:", "label: " + ibeacon.uniqueId)
                val landmark = getLocationFromBeacon(ibeacon.uniqueId)
                if (landmark != null) {
                    sendNotificationForLandmark(uniqueTracker as Context, landmark)
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackerService", "ON")

        uniqueTracker = this

        createNotificationChannel(this)

        val provider = if (BuildConfig.DEBUG) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER

        locationManager.requestLocationUpdates(provider, 10000, 10f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                checkPosition(location)
            }

            override fun onProviderDisabled(provider: String) {
                // Nothing TO DO
            }

            override fun onProviderEnabled(provider: String) {
                // Nothing TO DO
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                // Nothing TO DO
            }

        })

        proximityManager.setIBeaconListener(iBeaconListener)
        //Now scan start inside macroarea
        //beaconScanActivation()

        //sending new visited landmarks
        doAsync {
            val realm = Realm.getDefaultInstance()
            val lands = realm.where(Landmark::class.java).equalTo("visited", true).equalTo("sent", false).findAll()
            val labels = lands.map { it.beacon!!.label!! }
            if (labels.isNotEmpty()) {
                sendStatistics(application, labels, object : ResponseListener {
                    override fun onResponse(response: String) {
                        val res = Gson().fromJson(response, Response::class.java)
                        if (res.success) {
                            val realm = Realm.getDefaultInstance()
                            val lands = realm.where(Landmark::class.java).equalTo("visited", true).equalTo("sent", false).findAll()
                            realm.beginTransaction()
                            lands.setBoolean("sent", true)
                            realm.commitTransaction()
                        }
                    }
                }, object : ErrorListener {
                    override fun onError(error: String?) {
                        //nothing To Do
                    }
                })
            }
        }

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
        if (!proximityManager.isScanning)
            proximityManager.connect {
                proximityManager.startScanning()
            }
    }

    private fun beaconScanDeactivation() {
        if (proximityManager.isConnected) {
            proximityManager.disconnect()
        }
        /*DISABLING BLUETOOTH
        val bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth.isEnabled) {
            bluetooth.disable()
        }*/
    }

    private fun checkPosition(l: Location) {

        val realm = Realm.getDefaultInstance()
        val macroareas = realm.where(Macroarea::class.java).findAll()

        val area = Location("area")

        for (m in macroareas) {
            area.latitude = m.center?.latitude!!
            area.longitude = m.center?.longitude!!
            val distance = l.distanceTo(area)

            //we are inside a macroarea
            if (distance < m.radius!!) {
                if (currentArea != m) {
                    currentArea = m
                    beaconScanActivation()
                    //notify macroArea
                    sendNotificationForMacroarea(this, m)
                }
                return
            }
        }
        //we are outside a macroarea
        if (currentArea != null) {
            currentArea = null
            beaconScanDeactivation()
        }
    }

    private fun getLocationFromBeacon(beacon: String): Landmark? {
        val realm = Realm.getDefaultInstance()
        val land = realm.where(Landmark::class.java).equalTo("beacon.label", beacon).findFirst()
        //set landmark as visited
        if (land != null && !land.visited) {
            realm.beginTransaction()
            land.visited = true
            realm.commitTransaction()
        }
        return land
    }
}