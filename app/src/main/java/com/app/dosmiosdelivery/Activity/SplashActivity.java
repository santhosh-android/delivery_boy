package com.app.dosmiosdelivery.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.app.dosmiosdelivery.R;
import com.app.dosmiosdelivery.UserSessionManagement;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = UserSessionManagement.getUserSessionManagement(SplashActivity.this).isUserLoggedIn();
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    overridePendingTransition(R.anim.enter,R.anim.exit);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
