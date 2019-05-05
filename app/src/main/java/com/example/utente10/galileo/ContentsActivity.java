package com.example.utente10.galileo;

import android.content.Intent;
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

import com.example.utente10.galileo.bean.Landmark;
import com.example.utente10.galileo.bean.Macroarea;

import io.realm.Realm;

public class ContentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private android.support.v7.app.ActionBar actionbar;
    private ProgressBar progressBar;
    String landmarkLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        Bundle landmarkData = getIntent().getExtras();

        landmarkLabel = landmarkData.getString("landmarkLabel");

        // Query landmark selezionata
        Realm realm = Realm.getDefaultInstance();
        Landmark landmark = realm.where(Landmark.class).equalTo("beacon.label", landmarkLabel).findFirst();

        if (landmark == null)
            finish();

        else {

            toolbar = (Toolbar) findViewById(R.id.contents_toolbar);
            toolbar.setTitle(landmark.getName());
            setSupportActionBar(toolbar);
            actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);

            WebView myWebView = (WebView) findViewById(R.id.webview);
            WebSettings settings = myWebView.getSettings();
            myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            settings.setJavaScriptEnabled(true);

            //Progress bar mentre la pagina web sta caricando
            progressBar = new ProgressBar(ContentsActivity.this);
            progressBar = (ProgressBar) findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            //

            myWebView.setWebViewClient(new WebViewClient() {
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

            String contentUrl = landmark.getContent_url();
            myWebView.loadUrl(contentUrl);

            Button closeBtn = (Button) findViewById(R.id.curiosita_close);
            Button curiositaBtn = (Button) findViewById(R.id.curiosita_btn);
            RelativeLayout curiositaBox = (RelativeLayout) findViewById(R.id.curiosita);

            curiositaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curiositaBox.getVisibility() == View.GONE)
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

    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            Realm realm = Realm.getDefaultInstance();
            Macroarea macroarea = realm.where(Macroarea.class).contains("landmarks.beacon.label", landmarkLabel).findFirst();
            if (macroarea != null) {
                Intent intent = new Intent(getApplicationContext(), BeaconMapActivity.class);
                intent.putExtra("macroarea", macroarea.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finishAfterTransition();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
