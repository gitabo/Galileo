package com.example.utente10.galileo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        toolbar.setTitle("About GalileoPisaTour");
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    //Termina l'activity premendo il tasto indietro nella tooolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
