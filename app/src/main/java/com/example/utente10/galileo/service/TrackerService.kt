package com.example.utente10.galileo.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.kontakt.sdk.android.ble.manager.ProximityManager
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory
import com.kontakt.sdk.android.common.profile.IBeaconRegion
import com.kontakt.sdk.android.common.profile.IBeaconDevice
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener



class TrackerService : Service() {

    val proximityManager:ProximityManager by lazy {
        ProximityManagerFactory.create(this)
    }

    //Setting automatic restart after App is manually closed
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TrackerService", "ON")
        proximityManager.setIBeaconListener(createIBeaconListener())
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

    private fun beaconScanActivation(){

        //ENABLING BLUETOOTH
        val bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth.disable()){
            bluetooth.enable()
        }
        proximityManager.connect { proximityManager.startScanning() }
    }

    private fun createIBeaconListener(): IBeaconListener {
        return object : SimpleIBeaconListener() {
            override fun onIBeaconDiscovered(ibeacon: IBeaconDevice?, region: IBeaconRegion?) {
                Log.i("Found Beacon", "IBeacon discovered: " + ibeacon!!.toString())
                //TODO: cosa faccio quando trovo un beacon
            }
        }
    }
}
