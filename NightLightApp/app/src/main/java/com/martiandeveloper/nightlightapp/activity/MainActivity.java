package com.martiandeveloper.nightlightapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.martiandeveloper.nightlightapp.R;
import com.martiandeveloper.nightlightapp.service.MyService;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    public static int mBrightness;
    public static int mColor;
    private SeekBar seekBar;
    private Button startStop;
    TextView seekValue;
    private ConstraintLayout parent;
    private boolean isService;

    private Button pickBTN;
    int mDefaultColor;

    // Ads
    private AdView bannerAd;
    private AdRequest bannerAdRequest, interstitialAdRequest;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        initialize();
        arrangeAds();

        startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent in = new Intent(MainActivity.this, MyService.class);

                if (isService) {
                    stopService(in);
                    isService = false;
                    startStop.setText("Start");
                    seekBar.setVisibility(View.VISIBLE);
                    pickBTN.setVisibility(View.VISIBLE);
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.canDrawOverlays(MainActivity.this)) {
                            getApplicationContext().startService(in);
                            isService = true;
                            startStop.setText("Stop");
                            seekBar.setVisibility(View.INVISIBLE);
                            pickBTN.setVisibility(View.INVISIBLE);

                        } else {
                            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(myIntent);
                            seekBar.setVisibility(View.INVISIBLE);
                            pickBTN.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        getApplicationContext().startService(in);
                        isService = true;
                        startStop.setText("Stop");
                        seekBar.setVisibility(View.INVISIBLE);
                        pickBTN.setVisibility(View.INVISIBLE);
                    }


                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue,
                                          boolean fromUser) {


                seekValue.setText("Brightness " + progresValue / 2 + "%");
                mBrightness = progresValue;


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mDefaultColor = ContextCompat.getColor(MainActivity.this, R.color.colorPrimary);
        pickBTN = findViewById(R.id.pickBTN);
        pickBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(MainActivity.this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mDefaultColor = color;
                        mColor = color;
                        startStop.setVisibility(View.VISIBLE);
                    }
                });
                colorPicker.show();
            }
        });

    }

    private void arrangeAds(){
        MobileAds.initialize(this,getResources().getString(R.string.banner_ad));
        bannerAd = findViewById(R.id.adView);
        bannerAdRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(bannerAdRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad));
        interstitialAdRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(interstitialAdRequest);

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();

                interstitialAd.loadAd(interstitialAdRequest);
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    void initialize() {

        parent = findViewById(R.id.parent);


        seekBar = findViewById(R.id.seekBar1);
        startStop = findViewById(R.id.startStop);
        seekValue = findViewById(R.id.seekPercentage);
        seekValue.setText("Brightness " + mBrightness / 2 + "%)");

        seekBar.setMax(200);
        seekBar.setProgress(mBrightness);

        startStop.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent share_intent = new Intent(android.content.Intent.ACTION_SEND);
                share_intent.setType("text/plain");
                String shareBody = "Check out this application on Google Play:\nhttps://play.google.com/store/apps/details?id=com.martiandeveloper.nightlightapp";
                share_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Night Light");
                share_intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(share_intent, "Share via"));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.martiandeveloper.nightlightapp");
                Intent rate_intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(rate_intent);
                break;
            case R.id.other_apps:
                Uri uri2 = Uri.parse("https://play.google.com/store/apps/dev?id=6834191715264686209");
                Intent other_intent = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(other_intent);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

