package com.example.nasa3;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    final int delay = 5000; // ms
    final String[] urlArray = {"https://services.swpc.noaa.gov/images/animations/ovation/north/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/sdo-hmii/latest.jpg",
            "https://services.swpc.noaa.gov/images/planetary-k-index.gif",
            "https://services.swpc.noaa.gov/images/animations/lasco-c2/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/lasco-c3/latest.jpg"};
    ImageView imageView;
    int currentPicture = 0;
    Handler handler = new Handler();
    boolean runRunnable = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.imageView);
        loadPicture(urlArray[0]);
        handleTouchListener(imageView);
        createTimer();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleTouchListener(ImageView imageView) {
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                handleScrolling(1);

            }
            @Override
            public void onSwipeRight() {
                handleScrolling(-1);
            }
            @Override
            public void onDoubleClick() {
                if (!runRunnable) {
                    createTimer();
                }
                runRunnable = !runRunnable;
            }
        });
    }

    private void handleScrolling(int direction) {
        currentPicture = currentPicture + direction;
        if (currentPicture == urlArray.length) {
            currentPicture = 0;
        } else if (currentPicture == -1) {
            currentPicture = urlArray.length + currentPicture;
        }
        loadPicture(urlArray[currentPicture]);
    }

    private void createTimer() {
        handler.postDelayed(new Runnable() {
            public void run() {
                if (runRunnable) {
                    handler.postDelayed(this, delay);
                    handleScrolling(1);
                }
            }
        }, delay);
    }

    private void loadPicture(String url) {
        Glide.with(this).load(url).into(imageView);
    }
}