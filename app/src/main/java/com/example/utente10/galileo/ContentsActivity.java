package com.example.utente10.galileo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utente10.galileo.bean.Landmark;
import com.example.utente10.galileo.bean.Macroarea;
import com.google.android.gms.maps.model.LatLng;

import io.realm.Realm;
import io.realm.RealmResults;

public class ContentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private ProgressBar progressBar;
    private Bundle landmarkData;
    private String landmarkName;
    private String areaName;
    private Landmark landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        landmarkData = getIntent().getExtras();
        areaName = landmarkData.getString("macroarea");
        landmarkName = landmarkData.getString("landmark");

        toolbar = (Toolbar) findViewById(R.id.contents_toolbar);
        toolbar.setTitle(landmarkName);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);



        WebView myWebView =  (WebView) findViewById(R.id.webview);
        WebSettings settings = myWebView.getSettings();
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        settings.setJavaScriptEnabled(true);

       //Progress bar mentre la pagina web sta caricando
       progressBar = new ProgressBar(ContentsActivity.this);
       progressBar =(ProgressBar) findViewById(R.id.progressbar);
       progressBar.setVisibility(View.VISIBLE);
       //

       myWebView.setWebViewClient(new WebViewClient(){
           @Override
           public boolean shouldOverrideUrlLoading(WebView view, String url) {
               view.loadUrl(url);
               return true;
           }

           //Progress bar invisibile a pagina web caricata
           @Override
           public void onPageFinished(WebView view, String url) {
               if (progressBar.getVisibility() == View.VISIBLE)
                   progressBar.setVisibility(View.GONE);
           }
           //

           @Override
           public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
               //Toast.makeText(ContestActivity.this, "Error:" + description, Toast.LENGTH_SHORT).show();

           }
       });


        Realm realm = Realm.getDefaultInstance();
        // Query macroarea selezionata
        RealmResults<Macroarea> macroareas = realm.where(Macroarea.class).equalTo("name",areaName).findAll();

        for (Macroarea m : macroareas) {
            for (Landmark l : m.getLandmarks()) {
                if (l.getName().equals(landmarkName)) {
                    landmark = l;
                    break;
                }
            }
        }

        String contentUrl = landmark.getContent_url();
        myWebView.loadUrl(contentUrl);

        Button closeBtn = (Button)  findViewById(R.id.curiosita_close);
        Button curiositaBtn = (Button)  findViewById(R.id.curiosita_btn);
        RelativeLayout curiositaBox = (RelativeLayout) findViewById(R.id.curiosita);

        curiositaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curiositaBox.getVisibility() == View.GONE)
                    curiositaBox.setVisibility(View.VISIBLE);
                else
                    curiositaBox.setVisibility(View.GONE);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curiositaBox.setVisibility(View.GONE);
            }
        });

        String galileoText = landmark.getDescription();
        TextView curiositaText = (TextView) findViewById(R.id.curiosita_text);
        curiositaText.setText(galileoText);

    }

    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
