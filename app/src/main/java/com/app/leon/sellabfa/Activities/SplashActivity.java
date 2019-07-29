package com.app.leon.sellabfa.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.leon.sellabfa.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashScreenImageView)
    ImageView imageViewSplash;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout container;
    private boolean splashLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.splash_activity);
        if (!splashLoaded) {
            setContentView(R.layout.splash_activity);
            ButterKnife.bind(this);
            initialize();
            startSplash();//TODO 30 27 67
        } else {
            Intent goToLoginActivity = new Intent(SplashActivity.this, LoginActivity.class);
            goToLoginActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(goToLoginActivity);
            finish();
        }
    }

    private void initialize() {
        int splashResourceId = R.drawable.img_splash;
        imageViewSplash.setImageResource(splashResourceId);
    }

    private void startSplash() {
//        container.setRepeatCount(0);
//        container.setDuration(2800);
//        container.setBaseAlpha(0.5f);
//        container.setAutoStart(false);
//        container.setIntensity(1);
//        container.startShimmerAnimation();
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    splashLoaded = true;
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageViewSplash.setImageDrawable(null);
        container = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
