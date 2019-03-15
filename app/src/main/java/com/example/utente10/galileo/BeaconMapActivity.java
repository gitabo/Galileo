package com.example.utente10.galileo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente10.galileo.bean.Beacon;
import com.example.utente10.galileo.bean.Landmark;
import com.example.utente10.galileo.bean.Macroarea;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class BeaconMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private Bundle areaData;
    private String areaName;
    private BottomNavigationView infoNav;
    private Landmark landmark;
    //private List<Marker> markers;
    private LatLng pos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        areaData = getIntent().getExtras();
        areaName = areaData.getString("macroarea");

        toolbar = (Toolbar) findViewById(R.id.beacon_toolbar);
        toolbar.setTitle(areaName);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Button closeBtn = (Button) findViewById(R.id.close);
        RelativeLayout tutorialBox = (RelativeLayout) findViewById(R.id.tutorial_box);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialBox.setVisibility(GONE);
            }
        });

        if (savedInstanceState != null) {
            tutorialBox.setVisibility(GONE);
        }


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
        mMap.setPadding(0, 0, 0, 150);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setMyLocationEnabled(true);

        int i = 0;
        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        // Query macroarea selezionata
        Macroarea macroarea = realm.where(Macroarea.class).equalTo("name", areaName).findFirst();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Landmark l : macroarea.getLandmarks()) {
            pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
            mMap.addMarker(new MarkerOptions().title(l.getName()).position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker)));
            builder.include(pos);
            i++;
        }
        if (i < 2)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
        else {

            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            //googleMap.setOnMapLoadedCallback(() -> googleMap.animateCamera(cu));
            googleMap.setOnMapLoadedCallback(() -> googleMap.moveCamera(cu));
        }

        //UiSettings uiSettings = mMap.getUiSettings();
        //uiSettings.setZoomGesturesEnabled(false);

        //Gestione infoWindow
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.landmark_info, null);
                v.setBackgroundColor(R.color.colorPrimary);

                landmark = realm.where(Landmark.class).equalTo("beacon.coordinates.latitude",marker.getPosition().latitude).equalTo("beacon.coordinates.longitude",marker.getPosition().longitude).findFirst();

                ImageView im = (ImageView) v.findViewById(R.id.landmark_img);
                int imgId = getResources().getIdentifier(landmark.getImg_name(), "drawable", getPackageName());
                im.setImageResource(imgId);
                TextView areaTitle = (TextView) v.findViewById(R.id.landmark_title);
                Button b = (Button) v.findViewById(R.id.webcontent);
                b.setText("SCOPRI");

                String title = landmark.getName();
                //String informations = landmark.getDescription();
                //Visualizza nell'infowindow testo e desrizione del marker selezionato
                areaTitle.setText(title);
                //areaDescr.setText(informations);
                return v;
            }
        });

        //Gestione click sul marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showInformation(marker);
                return true;
            }
        });
        //

        //Click sull'infowindow
        mMap.setOnInfoWindowClickListener(marker -> {
            //infoNav.setVisibility(View.VISIBLE);
            if(landmark == null)
                landmark = realm.where(Landmark.class).equalTo("beacon.coordinates.latitude",marker.getPosition().latitude).equalTo("beacon.coordinates.longitude",marker.getPosition().longitude).findFirst();

            Intent i1 = new Intent(getApplicationContext(), ContentsActivity.class);
            i1.putExtra("landmarkLabel", landmark.getBeacon().getLabel());
            startActivity(i1);
        });
    }

    private void showInformation(Marker marker) {
        // Calcolo spostamento mappa per centrare l'infowindow
        RelativeLayout mapContainer = (RelativeLayout) findViewById(R.id.mapcontainer);
        int container_height = mapContainer.getHeight();

        Projection projection = mMap.getProjection();

        LatLng markerLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
        Point pointHalfScreenAbove = new Point(markerScreenPosition.x, markerScreenPosition.y - (container_height / 2));

        LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
        //

        marker.showInfoWindow();
        CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
        mMap.moveCamera(center);
    }

    //Termina l'activity premendo il tasto indietro nella tooolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
