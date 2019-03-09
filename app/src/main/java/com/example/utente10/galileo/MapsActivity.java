package com.example.utente10.galileo;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private List<Marker> markers;
    private Macroarea macroarea = null;
    private BottomNavigationView bottomNav;
    private Marker userPos;
    private float distance;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markers = new ArrayList<Marker>();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();

        //bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);

        //Mostra pulsante menu in alto a sinistra
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
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

        if(savedInstanceState != null){
            tutorialBox.setVisibility(View.GONE);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker and move the camera
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).findAll();
        int i=0;

        LatLng pos = null;
        for (Macroarea m : macroareas) {
            pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
            markers.add(mMap.addMarker(new MarkerOptions().position(pos).title(m.getName())));
            markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.galileo_marker));
            i++;
        }
        if (markers.size() > 1) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 1; // offset from edges of the map in pixels need to be different from zero
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            googleMap.animateCamera(cu);
            googleMap.setOnMapLoadedCallback(() -> googleMap.moveCamera(cu));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);


        //Gestione click sul marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override public boolean onMarkerClick(Marker marker) {
                showInformation(marker);
                return true;
                }
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
                    if(marker.getPosition().equals(pos)){
                        macroarea = m;
                        break;
                    }
                }

                ImageView im = (ImageView) v.findViewById(R.id.place_img);
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



        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //infoNav.setVisibility(View.VISIBLE);
                LatLng pos = null;
                //reindirizzamento a BeaconMapActivity
                for (Macroarea m : macroareas) {
                    pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
                    if(marker.getPosition().equals(pos)){
                        macroarea = m;
                        break;
                    }
                }
                Intent i = new Intent(getApplicationContext(), BeaconMapActivity.class);
                i.putExtra("macroarea",macroarea.getName());
                startActivity(i);
            }
        });

    }

    /*
    private void checkPosition(Location l){
        double gpsLat = l.getLatitude();
        double gpsLng = l.getLongitude();
        int i = 0;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).findAll();

        Location areaDest = new Location("areadest");

        for (Macroarea m : macroareas) {
            areaDest.setLatitude(m.getCenter().getLatitude());
            areaDest.setLongitude(m.getCenter().getLongitude());
            distance = l.distanceTo(areaDest);

            //Controllo se l'utente è a meno di 100 m da una macroarea
            //il caso che l'utente sia a meno di 100 m di più macroaree non viene gestito
            //perchè le macroaree sono abbastanza distanti tra loro
            if(distance<100){
                Toast.makeText(MapsActivity.this, "Rilevata area GalileoPisaTour", Toast.LENGTH_LONG).show();
                showInformation(markers.get(i));
                break;
            }
            i++;
        }
    }*/

    // Gestione apertura menu hamburger
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                //hideBottomNavigationView(bottomNav);
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

       LatLng pos = null;
       View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);

       Realm realm = Realm.getDefaultInstance();
       RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).findAll();

       for (Macroarea m : macroareas) {
           pos = new LatLng(m.getCenter().getLatitude(), m.getCenter().getLongitude());
           if(marker.getPosition().equals(pos)){
               macroarea = m;
               break;
           }
       }

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
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
