package com.jupiterjam;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity {
    public ImageView playerSprite;
    //Define sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // //determines direction of rocket (left or right tilt movement)
            float move = (float) Math.toDegrees(Math.atan2(x, y));
            move = -move;

            updatePlayerPosition(move);
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

 @Override
    protected void onCreate(Bundle savedInstanceState){
     super.onCreate(savedInstanceState);
     setContentView(R.layout.gameplay);

     // Initialize the sensor manager and actual sensor
     mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
     mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

     // Initialize player sprite
     playerSprite = findViewById(R.id.rocket);

 }

    //Generate a sensor event listener for the application
    //The sensor manager uses the sensorEventListener to track any changes in the accelerometer
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    private void updatePlayerPosition(float move) {

        // Move the player horizontally based on the tilt angle.
        float tiltSensitivity = 3f;
        float maxTiltDistance = 300f;
        float tiltDistanceTranslation = move * tiltSensitivity; //control how much movement occurs for a given tilt

        float moveThreshold = 1f;  // threshold value to ignore minor tilt movements
        float centerPosition = 0f;

        if (Math.abs(move) < moveThreshold) {
            move = 0;
            playerSprite.setTranslationX(centerPosition);
        }
        // Ensures the rocket doesn't go past set boundaries
        tiltDistanceTranslation = Math.max(-maxTiltDistance, Math.min(maxTiltDistance, tiltDistanceTranslation));

        // Update player's horizontal position

        playerSprite.setTranslationX(tiltDistanceTranslation);
    }

}
