package de.uni_weimar.mis.sensorsanddata;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.robinhood.spark.SparkView;

import android.os.Handler;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    TextView tvRate, tvWinSize;
    SensorManager sm;
    Sensor accel;
    ACCView accView;
    Magnitude magnitudeData = new Magnitude();
    SparkView sparkView;
    long lastUpdate, sampleRate;
    NotificationCompat.Builder notifyBuilder;
    NotificationManager notificationManager;
    int currentActivity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //cascading elements
        tvRate = (TextView) findViewById(R.id.tvSampleRate);
        tvWinSize = (TextView) findViewById(R.id.tvWinsize);
        SeekBar sbFFT = (SeekBar) findViewById(R.id.seekbarFFT);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sparkView = (SparkView) findViewById(R.id.sparkview);
        accView = (ACCView) findViewById(R.id.accView);
        SeekBar sbAcc = (SeekBar) findViewById(R.id.seekbarACC);
        magnitudeData.setWindowSize(5);
        sampleRate = 100;
        tvWinSize.setText("Window size: " + 5);
        tvRate.setText("Rate: " + 100 + "");
        accView.sensorData = magnitudeData.sensorData;
        //Seek bar listener
        sbAcc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sampleRate = progress;
                tvRate.setText("Rate: " + progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Seek bar listener
        sbFFT.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                magnitudeData.setWindowSize(progress);
                tvWinSize.setText("Window size: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (null == (accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))) {
            finish();
        }
        notifyBuilder =
                new NotificationCompat.Builder(this).setSmallIcon(R.drawable.sitting)
                        .setContentTitle("Activity").setContentText("Recognizing your activity...").setOngoing(true);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10815, notifyBuilder.build());
        //check Activity in background and change the notification
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        sleep(5000);
                        int activity = magnitudeData.GetActivity();
                        if (currentActivity != activity) {
                            int iconId = 0;
                            String message = "";
                            switch (activity) {
                                case 0:
                                    iconId = R.drawable.sitting;
                                    message = "You are sitting !";
                                    break;
                                case 1:
                                    iconId = R.drawable.walking;
                                    message = "You are walking !";
                                    break;
                                case 2:
                                    iconId = R.drawable.running;
                                    message = "You are running !";
                                    break;
                            }
                            notifyBuilder.setContentText(message);
                            notifyBuilder.setSmallIcon(iconId);
                            notificationManager.notify(10815, notifyBuilder.build());
                            currentActivity = activity;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        //We didn't unregister listener because we need calculate them in background also
        super.onPause();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        magnitudeData.calculateMagnitude(event);
        sparkView.setAdapter(new FFTAdapter(magnitudeData._FFTResult));
        sparkView.invalidate();
        long now = System.currentTimeMillis();
        if (now - lastUpdate > sampleRate) {
            accView.sensorData = magnitudeData.sensorData;
            accView.invalidate();
            lastUpdate = now;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
