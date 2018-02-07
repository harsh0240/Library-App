package com.example.user.librarytrial;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar mprogress = (ProgressBar) findViewById(R.id.progress);
        mprogress.setVisibility(View.VISIBLE);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        }, SPLASH_TIME_OUT);
    }

}
