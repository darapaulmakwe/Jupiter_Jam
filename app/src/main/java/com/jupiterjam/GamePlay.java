package com.jupiterjam;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GamePlay extends AppCompatActivity {
    // Define Player Object
    private Player player;
    private Enemy enemy;

    //Define Pause menu elements
    private boolean isPaused = false;
    ImageView pauseMenu;
    ImageButton pauseBtn;
    TextView pauseMenuText;
    public Button resumeButton;
    private Button homeButton;

    // Countdown elements
    private CountDownTimer countdownTimer;
    ImageView countdownTint;
    TextView countdownText;

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

     // Initialize countdown
     countdownTint = findViewById(R.id.countdownTint);
     countdownText = findViewById(R.id.countdownInt);
     startCountdown();

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
     //Initialize pause menu buttons
     pauseMenu = findViewById(R.id.pauseMenu);
     resumeButton = findViewById(R.id.resumeButton);
     homeButton = findViewById(R.id.homeButton);
     pauseBtn = findViewById(R.id.pauseButton);
     pauseMenuText = findViewById(R.id.textView);

     pauseBtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             pauseGame();
         }
     });
     resumeButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             resumeGame();
         }
     });


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

    public void startCountdown() {
         countdownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 == 0) {
                    countdownText.setText("Go!");
                } else {
                    countdownText.setText(String.valueOf(millisUntilFinished / 1000));
                }
                onPause();
            }

            public void onFinish() {
                countdownText.setVisibility(View.GONE);
                countdownTint.setVisibility(View.GONE);
                onResume();
            }
        };countdownTimer.start();
    }

    //Toggle functions for Pause Menu
    public void resumeGame(){
        pauseMenu.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        homeButton.setVisibility(View.GONE);
        pauseMenuText.setVisibility(View.GONE);
        onResume();
        isPaused = false;
    }
    public void pauseGame(){
        pauseMenu.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
        pauseMenuText.setVisibility(View.VISIBLE);
        onPause();
        isPaused = true;
    }

    public void revertToMainMenu(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }


}
