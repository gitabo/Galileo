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
import com.example.utente10.galileo.bean.Macroarea;
import com.example.utente10.galileo.service.TrackerService;
import com.google.gson.Gson;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    //    private Toolbar toolbar;
//    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;
//    private android.support.v7.app.ActionBar actionbar;

    Intent mServiceIntent;
    private static final int FINE_LOCATION_REQUEST = 1;
    private TrackerService tracker;
    public static boolean serviceEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cambio colore status bar
        setStatusBarColor();


        Realm realm = Realm.getDefaultInstance();

        //TODO: check db version
        if (realm.where(Macroarea.class).findAll().size() == 0) {
            BackendKt.getMacroareas(getApplication(),
                    response -> {
                        Gson gson = new Gson();
                        MacroareasResponse macroareasResponse = gson.fromJson(response, MacroareasResponse.class);
                        //Inserting data in db
                        realm.beginTransaction();
                        realm.insertOrUpdate(macroareasResponse.getMacroareas());
                        realm.commitTransaction();
                    },
                    error -> {
                    });
        }
        requestPermission();

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        actionbar = getSupportActionBar();
//
//        //Mostra pulsante menu in alto a sinistra
//        actionbar.setDisplayHomeAsUpEnabled(true);
//
//        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
//        setupDrawer();

//        Button btnAvvia = (Button)findViewById(R.id.btn_avvia);
//
//        btnAvvia.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
//                startActivity(i);
//            }
//        });

    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO: dire all'utente che deve abilitare la posizione se vuole utilizzare l'app
                //finish();
            } else {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_REQUEST);
            }
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

    // Cambia colore status bar
    private void setStatusBarColor() {

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDarker));

    }
    //

    /*** Gestione apertura menu ***/
//    private void setupDrawer() {
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        mDrawerLayout.addDrawerListener(mDrawerToggle);
//    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//       /* if (id == R.id.action_settings) {
//            return true;
//        }*/
//
//        // Activate the navigation drawer toggle
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }

}
