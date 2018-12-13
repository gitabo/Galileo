package com.example.utente10.galileo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.utente10.galileo.bean.Macroarea;
import com.example.utente10.galileo.service.TrackerService;
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
import kotlin.SinceKotlin;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private BottomNavigationView bottomNav;
    private BottomNavigationView infoNav;
    private Marker markerExample;
    private double distance;

    Intent mServiceIntent;
    private TrackerService tracker;
    public static boolean serviceEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Cambio colore status bar
        setStatusBarColor();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();

        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        infoNav = (BottomNavigationView) findViewById(R.id.info_nav);


        //Mostra pulsante menu in alto a sinistra
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        setupDrawer();

        BottomNavigationItemView navDistance = (BottomNavigationItemView) findViewById(R.id.nav_distance);
        BottomNavigationItemView navClose = (BottomNavigationItemView) findViewById(R.id.nav_close);

        //Visualizza testo sotto le icone nel banner che appare onInfoWindowClick
        BottomNavigationViewHelper.disableShiftMode(infoNav);

        navDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

        navClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoNav.setVisibility(View.GONE);
            }
        });

        //Tracker Activation
        tracker = new TrackerService();
        mServiceIntent = new Intent(this, tracker.getClass());
        if (serviceEnabled && !isTrackerServiceRunning()) {
            startService(mServiceIntent);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).findAll();

        LatLng pos = null;
        for (Macroarea m : macroareas) {
            pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
            markerExample = mMap.addMarker(new MarkerOptions().position(pos).title("Duomo di Pisa"));
            markerExample.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12.0f));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        //


        //Gestione click sul marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
                }
        });
        //

        //Gestione infoWindow
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);

                LatLng latLng = marker.getPosition();

                ImageView im = (ImageView) v.findViewById(R.id.imageView1);
                TextView tv1 = (TextView) v.findViewById(R.id.textView1);
                TextView tv2 = (TextView) v.findViewById(R.id.textView2);
                marker.setSnippet("Descrizione Descrizione Descrizione Descrizione Descrizione ");
                String title = marker.getTitle();
                String informations = marker.getSnippet();

                tv1.setText(title);
                tv2.setText(informations);
                return v;
            }
        });
        //

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                infoNav.setVisibility(View.VISIBLE);
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                infoNav.setVisibility(View.GONE);
            }
        });



    }




    //Cambia colore status bar
    private void setStatusBarColor(){

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDarker));

    }
    //

    // Gestione apertura menu hamburger
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                hideBottomNavigationView(bottomNav);
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                showBottomNavigationView(bottomNav);
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }
    //

    // Mostra menu distanze
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.distance_menu, popup.getMenu());
        popup.show();
    }
    //

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

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
    protected void onDestroy() {
        if (mServiceIntent != null)
        if (mServiceIntent != null)
            stopService(mServiceIntent);
        super.onDestroy();
    }
}