package com.example.utente10.galileo;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.utente10.galileo.bean.Beacon;
import com.example.utente10.galileo.bean.Macroarea;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

public class BeaconMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private Marker markerExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        toolbar = (Toolbar) findViewById(R.id.beacon_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.beacon_map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setPadding(0,0,0, 150);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Beacon> beacons = realm.where(Beacon.class).findAll();

        LatLng pos = null;
        for (Beacon b : beacons) {
            pos = new LatLng(b.getCoordinates().getLatitude(), b.getCoordinates().getLongitude());
            markerExample = mMap.addMarker(new MarkerOptions().position(pos).title("Esempio"));
            markerExample.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);
    }

    //Termina l'activity premendo il tasto indietro nella tooolbar
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
