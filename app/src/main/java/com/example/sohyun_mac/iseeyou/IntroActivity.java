package com.example.sohyun_mac.iseeyou;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class IntroActivity extends AppCompatActivity {

    private Handler introHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        final int INTRO_DELAY_MILLISECONDS = 2*1000;
        introHandler = new Handler();
        introHandler.postDelayed(introRun, INTRO_DELAY_MILLISECONDS);
    }

    Runnable introRun = new Runnable() {
        @Override
        public void run() {
            Intent startIntent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        introHandler.removeCallbacks(introRun);
    }
}
