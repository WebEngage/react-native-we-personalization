package com.webengage.sdk.android.actions.render;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.webengage.sdk.android.AnalyticsFactory;
import com.webengage.sdk.android.WebEngage;

public class WebEngageActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AnalyticsFactory.getAnalytics(this.getApplicationContext()).start(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        AnalyticsFactory.getAnalytics(this.getApplicationContext()).stop(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
