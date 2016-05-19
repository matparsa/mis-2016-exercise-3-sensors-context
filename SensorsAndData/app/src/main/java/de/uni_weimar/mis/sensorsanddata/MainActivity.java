package de.uni_weimar.mis.sensorsanddata;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{


    private TextView tvRate,tvWinSize;
    private SensorManager sm;
    private Sensor accel;
    private ACCView accView;
    private Magnitude magnitudeData=new Magnitude();
    SparkView sparkView;
    private long lastUpdate,sampleRate;
    NotificationCompat.Builder notifyBuilder;
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sparkView = (SparkView) findViewById(R.id.sparkview);
        magnitudeData.setWindowSize(5);
        sampleRate=100;

        tvRate=(TextView)findViewById(R.id.tvSampleRate);
        tvWinSize=(TextView)findViewById(R.id.tvWinsize);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        SeekBar sbAcc=(SeekBar)findViewById(R.id.seekbarACC);
        sbAcc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                sampleRate=progress;
                tvRate.setText("Rate: "+progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar sbFFT=(SeekBar)findViewById(R.id.seekbarFFT);
        sbFFT.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                magnitudeData.setWindowSize(progress);
                tvWinSize.setText("Window size: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvWinSize.setText("Window size: "+5);
        tvRate.setText("Rate: "+100+"");
        accView = (ACCView) findViewById(R.id.accView);
        accView.sensorData=magnitudeData.sensorData;
        if(null==(accel=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))){
            finish();
        }
        notifyBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.sand)
                        .setContentTitle("Current Activity")
                        .setOngoing(true)
                        .setContentText("Analyzing your Activity");
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(mId, mBuilder.build());
    }


    @Override
    public void onResume(){
        super.onResume();
        //Register Listener
        sm.registerListener(this,accel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause(){
        sm.unregisterListener(this);
        super.onPause();

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        magnitudeData.calculateMagnitude(event);
        sparkView.setAdapter(new FFTAdapter(magnitudeData._FFTResult));
        sparkView.invalidate();
        long now=System.currentTimeMillis();
        if(now-lastUpdate>sampleRate)
            {
                accView.sensorData = magnitudeData.sensorData;
                accView.invalidate();
                lastUpdate=now;
            }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
