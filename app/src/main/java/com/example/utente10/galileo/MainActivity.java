package com.example.utente10.galileo;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.utente10.galileo.backend.BackendKt;
import com.example.utente10.galileo.backend.MacroareasResponse;
import com.example.utente10.galileo.bean.DBVersion;
import com.example.utente10.galileo.bean.Macroarea;
import com.example.utente10.galileo.service.TrackerService;
import com.google.gson.Gson;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    Intent mServiceIntent;
    private static final int FINE_LOCATION_REQUEST = 1;
    private TrackerService tracker;
    public static boolean serviceEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm realm = Realm.getDefaultInstance();


        BackendKt.getDBVersion(getApplication(), response -> {
                    Gson gson = new Gson();
                    DBVersion dbv = gson.fromJson(response, DBVersion.class);
                    DBVersion db = realm.where(DBVersion.class).findFirst();
                    if (db == null || dbv.getVersion() > db.getVersion()) {
                        BackendKt.getMacroareas(getApplication(),
                                response2 -> {
                                    MacroareasResponse macroareasResponse = gson.fromJson(response2, MacroareasResponse.class);
                                    //Inserting data in db
                                    realm.beginTransaction();
                                    realm.insertOrUpdate(macroareasResponse.getMacroareas());
                                    realm.delete(DBVersion.class);
                                    realm.insert(dbv);
                                    realm.commitTransaction();
                                },
                                error -> {
                                });
                    }
                },
                error -> {
                });


        requestPermission();

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO: dire all'utente che deve abilitare la posizione se vuole utilizzare l'app
                //finish();
            } else {*/
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_REQUEST);
            //}
        } else {
            //Tracker Activation
            tracker = new TrackerService();
            mServiceIntent = new Intent(this, tracker.getClass());
            if (serviceEnabled && !isTrackerServiceRunning()) {
                startService(mServiceIntent);
            }

            // Dopo 2 secondi reindirizzamento a MapsActivity
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },
                    2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_REQUEST: {
                /* If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }*/
                requestPermission();
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private boolean isTrackerServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null)
            if (manager.getRunningServices(Integer.MAX_VALUE) != null)
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (TrackerService.class.getName().equals(service.service.getClassName()))
                        return true;
                }
        return false;
    }

}
