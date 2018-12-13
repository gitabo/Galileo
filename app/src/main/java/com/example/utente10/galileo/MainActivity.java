package com.example.utente10.galileo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends AppCompatActivity {

    //    private Toolbar toolbar;
//    private ActionBarDrawerToggle mDrawerToggle;
//    private DrawerLayout mDrawerLayout;
//    private android.support.v7.app.ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cambio colore status bar
        setStatusBarColor();


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
        //


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
