package com.nasaScroll;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    final int delay = 5000; // ms
    final String[] urlArray = {
            "https://services.swpc.noaa.gov/images/animations/ovation/north/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/sdo-hmii/latest.jpg",
            "https://services.swpc.noaa.gov/images/planetary-k-index.gif",
            "https://services.swpc.noaa.gov/images/animations/lasco-c2/latest.jpg",
            "https://services.swpc.noaa.gov/images/animations/lasco-c3/latest.jpg",
            "https://i.imgur.com/0JntFVC.gif"
    };
    //https://api.nasa.gov/planetary/apod?api_key=53qeq0jn2AkIhmcs7cLJZiKSkDxvPUgHqEXMJ4NS
    ImageView imageView;
    TextView scrollText;
    TextView APODText;
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
        //Don't do this
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView = findViewById(R.id.imageView);
        scrollText = findViewById(R.id.scrollText);
        scrollText.setText(R.string.ScrollingON);
        APODText = findViewById(R.id.APODText);
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
        if (currentPicture == 5) {
            try  {
                URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=53qeq0jn2AkIhmcs7cLJZiKSkDxvPUgHqEXMJ4NS");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "application/json");
                InputStream responseStream = connection.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                APOD apod = mapper.readValue(responseStream, APOD.class);
                urlArray[5] = apod.url;
                APODText.setText(apod.title);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            APODText.setText("");
        }
        loadPicture(urlArray[currentPicture]);
    }

    private void loadPicture(String url) {
        Glide.with(this).load(url).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(imageView);
    }
}