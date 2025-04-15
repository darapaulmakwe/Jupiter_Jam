package com.jupiterjam;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Iterator;
import java.util.Random;

public class GamePlay extends AppCompatActivity{
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

    // Define asteroid items
    private FrameLayout asteroidLayout; // Container for asteroids
    private ArrayList<Asteroid> asteroids; // List to hold active asteroids
    private Handler asteroidHandler; // Handler for timing and updates
    private Runnable asteroidRunnable;
    private Random random;
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();

    private ArrayList<Bullet> playerBullets = new ArrayList<>();


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

     ConstraintLayout gameLayout = findViewById(R.id.gameLayout);
     // Initialize player sprite
     ImageView playerSprite = findViewById(R.id.rocket);

     ImageView bulletSprite = findViewById(R.id.bullet);
     bulletSprite.setVisibility(View.INVISIBLE);

     ImageView enemySprite = findViewById(R.id.enemy);
     player = new Player(playerSprite, bulletSprite);
     gameLayout.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
             ImageView bullet = new ImageView(GamePlay.this);
             bullet.setVisibility(View.INVISIBLE);
             bullet.setImageResource(R.drawable.laser_bullet);

             int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                     114, getResources().getDisplayMetrics());
             //Layout parameters for the bullet
             ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(size, size);

             //Sets constraints
             layoutParams.bottomToBottom = R.id.asteroidLayout;
             layoutParams.topToTop = R.id.asteroidLayout;
             layoutParams.startToStart = R.id.asteroidLayout;
             layoutParams.horizontalBias = 0.498f;

             // Apply to image view
             bullet.setLayoutParams(layoutParams);

             // Apply to the layout
             gameLayout.addView(bullet);
             player.setBulletView(bullet);
             Bullet newBullet = player.shoot();
             playerBullets.add(newBullet);
         }
     });
     enemy =  new Enemy(enemySprite, gameLayout,400f,-300f);
     enemy.setBulletRegisterCallback(new Enemy.BulletRegisterCallback() {
         @Override
         public void enemyBullet(Bullet bullet) {
             enemyBullets.add(bullet);
         }
     });
     enemy.startShooting();
     enemyRunnable = new Runnable() {
         @Override
         public void run() {
             if (enemy != null){
                 enemy.updateEnemyPosition();
                 enemyHit(enemy,playerBullets);
                 playerHit(player,enemyBullets);

             }
             Iterator<Bullet> playerIterator = playerBullets.iterator();
             while(playerIterator.hasNext()){
                 Bullet bullet = playerIterator.next();
                 if(!bullet.getSpriteView().isShown()){
                     playerIterator.remove();
                 }
             }

             Iterator<Bullet> enemyIterator = enemyBullets.iterator();
             while(enemyIterator.hasNext()){
                 Bullet bullet = enemyIterator.next();
                 if(!bullet.getSpriteView().isShown()){
                     enemyIterator.remove();
                 }
             }


             enemyHandler.postDelayed(this,17); // need to edit the speed
         }
     };

     asteroidLayout = (FrameLayout) findViewById(R.id.asteroidLayout);
     asteroids = new ArrayList<>();
     asteroidHandler = new Handler();
     random = new Random();

     // Start spawning asteroids
     spawnAsteroids();

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

    // Method to spawn new asteroids at random intervals
    private void spawnAsteroids() {
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Runnable to spawn new asteroids every 1-3 seconds
        asteroidHandler.postDelayed(asteroidRunnable = new Runnable() {
            @Override
            public void run() {
                // Create a new asteroid and add it to the layout
                Asteroid newAsteroid = new Asteroid(asteroidLayout, screenWidth, screenHeight);
                asteroids.add(newAsteroid);

                // Clean up destroyed asteroids
                for (int i = 0; i < asteroids.size(); i++) {
                    if (asteroids.get(i).isDestroyed()) {
                        asteroids.remove(i);
                        i--;
                    }
                }
                // Schedule the next asteroid spawn
                asteroidHandler.postDelayed(this, random.nextInt(2000) + 1000); // Random interval (2 to 5 seconds)
            }
        }, random.nextInt(2000) + 1000); // Initial delay (1 to 3 seconds)
    }

    //Generate a sensor event listener for the application
    //The sensor manager uses the sensorEventListener to track any changes in the accelerometer
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        enemyHandler.post(enemyRunnable);

        asteroidHandler.post(asteroidRunnable);
        //Resumes asteroid animations individually through asteroid class
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).resumeAsteroid();
        }
        // resumes enemy shooting
        enemy.startShooting();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
        enemyHandler.removeCallbacks(enemyRunnable);

        asteroidHandler.removeCallbacks(asteroidRunnable);
        // Pauses asteroid animations individually through asteroid class
        for (int i = 0; i < asteroids.size(); i++) {
            asteroids.get(i).pauseAsteroid();
        }
        // Pauses enemy shooting
        enemy.stopShooting();

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

    private void enemyHit(Enemy enemy, ArrayList<Bullet> bullets){
        Iterator<Bullet> iterator = bullets.iterator();
        while(iterator.hasNext()){
            Bullet bullet = iterator.next();
            if(bullet.hitTarget(enemy.getX(), enemy.getY(),enemy.getHeight(),enemy.getWidth())){
                enemy.gotHit();
                bullet.stopBullet();
                iterator.remove();
                break;

            }
        }
    }
    private void playerHit(Player player, ArrayList<Bullet> bullets){
        Iterator<Bullet> iterator = bullets.iterator();
        while(iterator.hasNext()){
            Bullet bullet = iterator.next();
            if(bullet.hitTarget(player.getX(), player.getY(), player.getHeight(), player.getWidth())){
                player.gotHit();
                bullet.stopBullet();
                iterator.remove();
                break;
            }

        }
    }






}
