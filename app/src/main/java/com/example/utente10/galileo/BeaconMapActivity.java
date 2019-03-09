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
    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        areaData = getIntent().getExtras();
        areaName = areaData.getString("macroarea");

        //infoNav = (BottomNavigationView) findViewById(R.id.info_nav);
        //infoNav.setVisibility(View.GONE);

        markers = new ArrayList<Marker>();
        //infoNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        //infoNav.setSelectedItemId(R.id.info_nav);

        //Visualizza testo sotto le icone della bottomnavigationview
        //BottomNavigationViewHelper.disableShiftMode(infoNav);

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

        if(savedInstanceState != null){
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
        mMap.setPadding(0,0,0, 150);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        int i = 0;
        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        // Query macroarea selezionata
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).equalTo("name",areaName).findAll();

        // Memorizzazione nell'array landmarks dei Beacon della macroarea


        LatLng pos = null;
        for(Macroarea m : macroareas) {
            for (Landmark l : m.getLandmarks()) {
                pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
                markers.add(mMap.addMarker(new MarkerOptions().position(pos).title(l.getName())));
                markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
                i++;
            }
        }

        if (i < 1)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
        else {
            //Center all markers in the map
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }

            LatLngBounds bounds = builder.build();
            int padding = 40; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(false);

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
        //

        //Gestione click sul marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override public boolean onMarkerClick(Marker marker) {
                showInformation(marker);
                return true;
            }
        });
        //

        //Click sull'infowindow
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //infoNav.setVisibility(View.VISIBLE);
                LatLng pos = null;
                //reindirizzamento a BeaconMapActivity
                for (Macroarea m : macroareas) {
                    for (Landmark l : m.getLandmarks()) {
                        pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
                        if (marker.getPosition().equals(pos)) {
                            landmark = l;
                            break;
                        }
                    }
                }
                Intent i = new Intent(getApplicationContext(), ContentsActivity.class);
                i.putExtra("landmarkLabel",landmark.getBeacon().getLabel());
                startActivity(i);
            }
        });
        //

        /*
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
        */
    }

    private void showInformation(Marker marker){
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
