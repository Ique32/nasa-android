package com.nasaScroll;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

public class MainActivity extends AppCompatActivity {

    final int delay = 5000; // ms
    final String[] urlArray = {
            "https://services.swpc.noaa.gov/images/animations/ovation/north/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/sdo-hmii/latest.jpg",
            "https://services.swpc.noaa.gov/images/planetary-k-index.gif",
            "https://services.swpc.noaa.gov/images/animations/lasco-c2/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/lasco-c3/latest.jpg"
    };
    ImageView imageView;
    TextView scrollText;
    int currentPicture = 0;
    boolean runRunnable = true;
    Handler handler = new Handler(Looper.getMainLooper());
    final Runnable runnable = new Runnable() {
        public void run() {
            handler.postDelayed(this, delay);
            handleScrolling(1);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();

        imageView = findViewById(R.id.imageView);
        scrollText = findViewById(R.id.scrollText);
        scrollText.setText(R.string.ScrollingON);
        loadPicture(urlArray[0]);
        handleTouchListener(imageView);
        startRunnable();
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
                runRunnable = !runRunnable;
                if (runRunnable) {
                    scrollText.setText(R.string.ScrollingON);
                    startRunnable();
                } else {
                    scrollText.setText(R.string.ScrollingOFF);
                    stopRunnable();
                }
            }
        });
    }

    private void stopRunnable() {
        handler.removeCallbacks(runnable);
    }

    private void startRunnable() {
        handler.postDelayed(runnable, delay);
    }

    private void resetRunnable() {
        stopRunnable();
        startRunnable();
    }

    private void handleScrolling(int direction) {
        resetRunnable();
        currentPicture = currentPicture + direction;
        if (currentPicture == urlArray.length) {
            currentPicture = 0;
        } else if (currentPicture == -1) {
            currentPicture = urlArray.length - 1;
        }
        loadPicture(urlArray[currentPicture]);
    }

    private void loadPicture(String url) {
        Glide.with(this).load(url).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(imageView);
    }
}