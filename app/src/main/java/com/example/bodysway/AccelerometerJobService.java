package com.example.bodysway;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerJobService extends JobService implements SensorEventListener {
    private static final String TAG = "AccelerometerJobService";
    private boolean jobCancelled = false;
    private static final int ACCELEROMETER_JOB_ID = 1;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float ax, ay, az;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStart AccelerometerJobService");sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //initier les sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        //arreter le sensor lorsque le job est arreté
        //sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, accelerometer);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Long tsLong = System.currentTimeMillis()/1000;
        String timestampString = tsLong.toString();
        Log.d(TAG, "timestamp: " + timestampString + "\naX: " + event.values[0] + "\naY: " + event.values[1] + "\naZ: " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
