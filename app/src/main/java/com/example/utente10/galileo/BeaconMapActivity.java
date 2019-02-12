package com.example.utente10.galileo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utente10.galileo.bean.Beacon;
import com.example.utente10.galileo.bean.Landmark;
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
    private Marker beaconMarker;
    private Bundle areaData;
    private String areaName;
    private BottomNavigationView infoNav;
    private Landmark landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        areaData = getIntent().getExtras();
        areaName = areaData.getString("macroarea");

        infoNav = (BottomNavigationView) findViewById(R.id.info_nav);
        infoNav.setVisibility(View.GONE);
        //infoNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        //infoNav.setSelectedItemId(R.id.info_nav);

        //Visualizza testo sotto le icone della bottomnavigationview
        //BottomNavigationViewHelper.disableShiftMode(infoNav);

        toolbar = (Toolbar) findViewById(R.id.beacon_toolbar);
        toolbar.setTitle(areaName);
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
        // Query macroarea selezionata
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).equalTo("name",areaName).findAll();

        // Memorizzazione nell'array landmarks dei Beacon della macroarea


        LatLng pos = null;
        for(Macroarea m : macroareas) {
            for (Landmark l : m.getLandmarks()) {
                pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
                beaconMarker = mMap.addMarker(new MarkerOptions().position(pos).title(l.getName()));
                beaconMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);

        //Gestione infoWindow
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);
                LatLng pos = null;

                for (Macroarea m : macroareas) {
                    for (Landmark l : m.getLandmarks()) {
                        pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
                        if (marker.getPosition().equals(pos)) {
                            landmark = l;
                            break;
                        }
                    }
                }
                    ImageView im = (ImageView) v.findViewById(R.id.place_img);
                    TextView areaTitle = (TextView) v.findViewById(R.id.place_title);
                    TextView areaDescr = (TextView) v.findViewById(R.id.place_descr);
                    Button b = (Button) v.findViewById(R.id.go_tour);
                    b.setVisibility(View.GONE);

                    String title = landmark.getName();
                    String informations = landmark.getDescription();
                    //Visualizza nell'infowindow testo e desrizione del marker selezionato
                    areaTitle.setText(title);
                    areaDescr.setText(informations);

                    return v;
            }
        });

        //Gestione click sul marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override public boolean onMarkerClick(Marker marker) {
                if(infoNav.getVisibility() == View.GONE)
                    infoNav.setVisibility(View.VISIBLE);
                showBottomNavigationView(infoNav);
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });
        //

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                hideBottomNavigationView(infoNav);
            }
        });

        infoNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i = new Intent(getApplicationContext(), ContentsActivity.class);
                //i.putExtra("macroarea",macroarea.getName());

                switch (item.getItemId()) {
                    case R.id.nav_info:
                        startActivity(i);
                        break;
                    case R.id.nav_img:
                        startActivity(i);
                        break;
                    case R.id.nav_video:
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
    }

    //Termina l'activity premendo il tasto indietro nella tooolbar
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }
}
