package com.martiandeveloper.nightlightapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.martiandeveloper.nightlightapp.activity.MainActivity;


public class MyService extends Service {

    private static final String TAG = "Darkness";
    private static WindowManager wm;
    private static WindowManager.LayoutParams wmParams;
    private static LinearLayout myView;
    private int BRIGHTNESS = 0;
    private int color = android.R.color.black;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        BRIGHTNESS = MainActivity.mBrightness;
        color = MainActivity.mColor;
        createView();
        Log.i(TAG, "Service Started");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(myView);
    }


    private void createView() {

        myView = new LinearLayout(getApplicationContext());
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT);


        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        myView.setBackgroundColor(MainActivity.mColor);


        wmParams.alpha = (1.0f - ((BRIGHTNESS + 55) / (float) 255));
        myView.setClickable(false);
        wm.addView(myView, wmParams);

    }


}

