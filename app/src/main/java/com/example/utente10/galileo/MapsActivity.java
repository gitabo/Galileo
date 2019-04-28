package com.example.utente10.galileo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import io.realm.Realm;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private Macroarea macroarea = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();

        //Mostra pulsante menu in alto a sinistra
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setupDrawer();

        Button closeBtn = (Button) findViewById(R.id.close);
        Button explore = (Button) findViewById(R.id.go_tour);
        RelativeLayout tutorialBox = (RelativeLayout) findViewById(R.id.tutorial_box);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorialBox.setVisibility(View.GONE);
            }
        });

        if (savedInstanceState != null) {
            tutorialBox.setVisibility(View.GONE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Gestione click item del menu laterale
        NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_about:
                        Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.nav_stats:
                        Intent statsIntent = new Intent(getApplicationContext(), StatisticsActivity.class);
                        startActivity(statsIntent);
                        break;
                }
                return false;
            }
        });
        //
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

        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).findAll();
        int i = 0;

        LatLng pos = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Macroarea m : macroareas) {
            pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
            mMap.addMarker(new MarkerOptions().title(m.getName()).position(pos).icon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker)));
            builder.include(pos);

            //Show area around macroarea marker
            Circle circle = mMap.addCircle(new CircleOptions()
                    .center(pos)
                    .radius(m.getRadius())
                    .strokeColor(Color.RED)
                    .strokeWidth(3f)
                    .fillColor(Color.argb(70,150,50,50)));

            i++;
        }
        if (i > 1) {
            LatLngBounds bounds = builder.build();
            int padding = 80; // offset from edges of the map in pixels need to be different from zero
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.setOnMapLoadedCallback(() -> googleMap.moveCamera(cu));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14.0f));
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);


        //Gestione click sul marker
        mMap.setOnMarkerClickListener(marker -> {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_clicked));
            showInformation(marker);
            return true;
        });
        //

        //Gestione infoWindow
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);
                LatLng pos = null;

                for (Macroarea m : macroareas) {
                    pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
                    if (marker.getPosition().equals(pos)) {
                        macroarea = m;
                        break;
                    }
                }

                ImageView im = (ImageView) v.findViewById(R.id.place_img);
                int imgId = getResources().getIdentifier(macroarea.getImg(), "drawable", getPackageName());
                im.setImageResource(imgId);
                TextView areaTitle = (TextView) v.findViewById(R.id.place_title);
                TextView areaDescr = (TextView) v.findViewById(R.id.place_descr);
                String title = macroarea.getName();
                String informations = macroarea.getDescription();
                //Visualizza nell'infowindow testo e desrizione del marker selezionato
                areaTitle.setText(title);
                areaDescr.setText(informations);
                areaDescr.setMovementMethod(new ScrollingMovementMethod());

                return v;
            }
        });
        //


        mMap.setOnInfoWindowClickListener(marker -> {
            //infoNav.setVisibility(View.VISIBLE);
            LatLng pos1 = null;

            //reindirizzamento a BeaconMapActivity

            macroarea = realm.where(Macroarea.class).equalTo("center.latitude",marker.getPosition().latitude).equalTo("center.longitude",marker.getPosition().longitude).findFirst();

            Intent intent = new Intent(getApplicationContext(), BeaconMapActivity.class);
            intent.putExtra("macroarea", macroarea.getName());
            startActivity(intent);
        });

        mMap.setOnInfoWindowCloseListener(marker -> {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
        });

    }

    // Gestione apertura menu hamburger
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //hideBottomNavigationView(bottomNav);
                drawerView.bringToFront();
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                //showBottomNavigationView(bottomNav);
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }
    //

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

        LatLng pos = null;
        View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);

        Realm realm = Realm.getDefaultInstance();
        if (macroarea == null)
            macroarea = realm.where(Macroarea.class).equalTo("center.latitude", marker.getPosition().latitude).equalTo("center.longitude", marker.getPosition().longitude).findFirst();

        ImageView im = (ImageView) v.findViewById(R.id.place_img);
        TextView areaTitle = (TextView) v.findViewById(R.id.place_title);
        TextView areaDescr = (TextView) v.findViewById(R.id.place_descr);
        String title = macroarea.getName();
        String informations = macroarea.getDescription();
        //Visualizza nell'infowindow testo e desrizione del marker selezionato
        areaTitle.setText(title);
        areaDescr.setText(informations);
        areaDescr.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
