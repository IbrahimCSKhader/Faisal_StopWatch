package com.cscorner.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime;
    private Button btnStart, btnStop, btnReset;
    private Handler handler;
    private long startTime = 0L, timeSwapBuff = 0L, updateTime = 0L;
    private boolean isRunning = false;
    private Runnable updateTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        handler = new Handler();

        updateTimerThread = new Runnable() {
            public void run() {
                updateTime = timeSwapBuff + (isRunning ? SystemClock.uptimeMillis() - startTime : 0);
                updateDisplay();
                handler.postDelayed(this, 100);
            }
        };

        if (savedInstanceState != null) {
            startTime = savedInstanceState.getLong("startTime");
            timeSwapBuff = savedInstanceState.getLong("timeSwapBuff");
            isRunning = savedInstanceState.getBoolean("isRunning");

            if (isRunning) {
                startTime = SystemClock.uptimeMillis() - savedInstanceState.getLong("elapsedTime");
                handler.post(updateTimerThread);
            }
        }

        btnStart.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = SystemClock.uptimeMillis();
                handler.post(updateTimerThread);
                isRunning = true;
            }
            updateButtonStates(false, true, true);
        });

        btnStop.setOnClickListener(v -> {
            if (isRunning) {
                timeSwapBuff += SystemClock.uptimeMillis() - startTime;
                handler.removeCallbacks(updateTimerThread);
                isRunning = false;
            }
            updateButtonStates(true, false, true);
        });

        btnReset.setOnClickListener(v -> {
            startTime = timeSwapBuff = updateTime = 0L;
            updateDisplay();
            handler.removeCallbacks(updateTimerThread);
            isRunning = false;
            updateButtonStates(true, false, false);
        });

        updateDisplay();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("startTime", startTime);
        outState.putLong("timeSwapBuff", timeSwapBuff);
        outState.putBoolean("isRunning", isRunning);
        outState.putLong("elapsedTime", isRunning ? SystemClock.uptimeMillis() - startTime : 0);
    }

    private void updateDisplay() {
        int secs = (int) (updateTime / 1000);
        int mins = secs / 60;
        secs %= 60;
        tvTime.setText(String.format("%02d:%02d", mins, secs));
    }

    private void updateButtonStates(boolean start, boolean stop, boolean reset) {
        btnStart.setEnabled(start);
        btnStop.setEnabled(stop);
        btnReset.setEnabled(reset);
    }
}
