package edu.cmu.pocketsphinx.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout logoLayout;
    private ImageView logoIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        logoLayout = findViewById(R.id.logolayout);
        logoIcon = findViewById(R.id.logoicon);

        Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        logoIcon.startAnimation(myAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 5*1000);
    }

}
