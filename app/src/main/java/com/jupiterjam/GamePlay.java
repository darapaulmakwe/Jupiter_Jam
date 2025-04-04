package com.jupiterjam;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity {
    // Define Player Object
    private Player player;
    private Enemy enemy;

    //Define sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private Handler enemyHandler = new Handler();
    private Runnable enemyRunnable;

    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float rawXAcceleration = event.values[0]; // Get raw X-axis value

                //Tell the Player to update its position
                if (player != null) {
                    player.updatePosition(rawXAcceleration); // Pass raw data to the Player
                }
            }
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
     ImageView playerSprite = findViewById(R.id.rocket);
     ImageView enemySprite = findViewById(R.id.enemy);
     player = new Player(playerSprite);
     enemy =  new Enemy(enemySprite,400f,-300f);
     enemyRunnable = new Runnable() {
         @Override
         public void run() {
             if (enemy != null){
                 enemy.updateEnemyPosition();
             }
             enemyHandler.postDelayed(this,17); // need to edit the speed
         }
     };


 }

    //Generate a sensor event listener for the application
    //The sensor manager uses the sensorEventListener to track any changes in the accelerometer
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        enemyHandler.post(enemyRunnable);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
        enemyHandler.removeCallbacks(enemyRunnable);

    }

}
