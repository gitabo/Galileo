package com.example.utente10.galileo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class StatisticsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        toolbar.setTitle("Statistiche visite");
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        WebView myWebView = (WebView) findViewById(R.id.stats_webview);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.loadUrl("http://192.168.1.30:8080/GalileoServer/statistics");
    }

    //Termina l'activity premendo il tasto indietro nella tooolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
