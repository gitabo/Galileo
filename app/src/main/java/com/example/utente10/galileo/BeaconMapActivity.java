package com.example.utente10.galileo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private Landmark landmark;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_map);

        density = this.getResources().getDisplayMetrics().density;

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
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setMyLocationEnabled(true);
        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        // Query macroarea selezionata
        Macroarea macroarea = realm.where(Macroarea.class).equalTo("name", areaName).findFirst();

        LinearLayout landmarksList = (LinearLayout)findViewById(R.id.landmarks_list);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        //Animation onclick scrollview item
        TypedValue outValue = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        //

        mMap.setOnMapLoadedCallback(() -> {
            int i = 0;
            LatLng pos = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();


            for (Landmark l : macroarea.getLandmarks()) {
                //Add marker to the map
                pos = new LatLng(l.getBeacon().getCoordinates().getLatitude(), l.getBeacon().getCoordinates().getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().title(l.getName()).position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker)));
                builder.include(pos);
                //

                /*** Add landmark to the ScrollView linearlayout ***/
                LinearLayout landmarkItem = new LinearLayout(this);
                float height = getResources().getDimension(R.dimen.scrollview_item);
                landmarkItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)height));
                landmarkItem.setGravity(Gravity.CENTER_VERTICAL);

                ImageView placeIcon = new ImageView(this);
                placeIcon.setImageResource(R.drawable.ic_place_black_24dp);
                placeIcon.setColorFilter(Color.WHITE);
                height = getResources().getDimension(R.dimen.scrollview_icon);
                placeIcon.setLayoutParams(new LinearLayout.LayoutParams((int)height, (int)height));

                TextView landmarkText = new TextView(this);
                landmarkText.setText(l.getName());
                landmarkText.setTextColor(Color.WHITE);
                landmarkText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                landmarkText.setTypeface(tf);
                landmarkText.setGravity(Gravity.CENTER_VERTICAL);
                landmarkText.setForegroundGravity(Gravity.CENTER_VERTICAL);
                landmarkItem.setOrientation(LinearLayout.HORIZONTAL);
                landmarkItem.addView(placeIcon);
                landmarkItem.addView(landmarkText);
                landmarkItem.setBackgroundResource(outValue.resourceId);
                landmarkItem.setClickable(true);
                landmarkItem.setFocusable(true);
                landmarksList.addView(landmarkItem);
                /***/

                //Focus on the marker when user selects a landmark from the list
                landmarkItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_clicked));
                        showInformation(marker);
                    }
                });
                //

                i++;
            }
            if (i < 2)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17.0f));
            else {
                LatLngBounds bounds = builder.build();
                int padding = 60; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        });

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

                landmark = realm.where(Landmark.class).equalTo("beacon.coordinates.latitude", marker.getPosition().latitude).equalTo("beacon.coordinates.longitude", marker.getPosition().longitude).findFirst();

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
        mMap.setOnMarkerClickListener(marker -> {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_clicked));
            showInformation(marker);
            return true;
        });
        //

        //Click sull'infowindow
        mMap.setOnInfoWindowClickListener(marker -> {
            //infoNav.setVisibility(View.VISIBLE);
            if (landmark == null)
                landmark = realm.where(Landmark.class).equalTo("beacon.coordinates.latitude", marker.getPosition().latitude).equalTo("beacon.coordinates.longitude", marker.getPosition().longitude).findFirst();

            Intent i1 = new Intent(getApplicationContext(), ContentsActivity.class);
            i1.putExtra("landmarkLabel", landmark.getBeacon().getLabel());
            startActivity(i1);
        });

        mMap.setOnInfoWindowCloseListener(marker -> {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
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
