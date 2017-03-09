package ccoderad.bnds.shiyiquanevent.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import ccoderad.bnds.shiyiquanevent.R;
import ccoderad.bnds.shiyiquanevent.global.PreferencesConstants;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences pr = getSharedPreferences(PreferencesConstants.SETTING_PREF, MODE_PRIVATE);
        if (!pr.getBoolean(PreferencesConstants.SETTING_SKIP_SPLASH_SCREEN_TAG, false)) {
            Handler delay = new Handler();
            delay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    SplashScreen.this.finish();
                }
            }, 2000);
        } else {
            this.finish();
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
        }
    }
}
