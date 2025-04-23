package com.jupiterjam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class GamePlay extends AppCompatActivity{
    // Define Player Object
    private Player player;
    private long lastShotTime = 0;
    private final long shootDelay = 500;
    private ProgressBar healthProgressBar;
    private Enemy enemy;

    private ProgressBar enemyHealthProgressBar;

    //Define Pause menu elements
    public boolean isPaused = false;
    public boolean isGameActive = false;
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

    // Define active player and enemy bullet lists
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();
    private ArrayList<Bullet> playerBullets = new ArrayList<>();

    private String difficulty;
    private Random random;
    public SharedPreferences prefs;


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

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
     difficulty = getIntent().getStringExtra("difficulty");

     // Initialize countdown
     countdownTint = findViewById(R.id.countdownTint);
     countdownText = findViewById(R.id.countdownInt);
     pauseBtn = findViewById(R.id.pauseButton);
     startCountdownTimer();

     // Initialize the sensor manager and actual sensor
     mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
     mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

     ConstraintLayout gameLayout = findViewById(R.id.gameLayout);

     // Initialize player sprite
     ImageView playerSprite = findViewById(R.id.rocket);
     initPlayerSprite(playerSprite);

     // Initialize call shooting and creation functions
     ImageView bulletSprite = findViewById(R.id.bullet);
     healthProgressBar = findViewById(R.id.healthProgressBar);
     createPlayer(playerSprite,bulletSprite, healthProgressBar);
     playerShooting(gameLayout);

     // Initialize asteroid tools
     asteroidLayout = (FrameLayout) findViewById(R.id.asteroidLayout);
     asteroids = new ArrayList<>();
     asteroidHandler = new Handler();
     random = new Random();
     // Start spawning asteroids
     spawnAsteroids();


     // create enemy sprite and call functions to start game loop
     ImageView enemySprite = findViewById(R.id.enemy);
     enemyHealthProgressBar = findViewById(R.id.enemyHealthProgressBar);
     enemyCreate(enemySprite,gameLayout, enemyHealthProgressBar);
     enemyRunnable();
     playerDeathListener();
     enemyHandler.post(enemyRunnable);


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

    /**
     * Method to handle initializing the players chosen theme. Loads the users shared preferences
     * and sets the theme. If no theme has been previously chosen it will default to the jupiter-ship
     * model. Sets the ImageView that will be used throughout gameplay to represent the player.
     *
     * @param playerSprite : ImageView for the players avatar to be displayed in.
     */
    private void initPlayerSprite(ImageView playerSprite) {
        // Getting Player preferences for theme
        SharedPreferences prefs = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);
        int rocketResId = prefs.getInt("selectedTheme", R.drawable.jupiter_ship); // fallback default
        playerSprite.setImageResource(rocketResId);
    }

    /**
     * Method to spawn new asteroids at random intervals. Uses a runnable to create an asteroid
     * and add it to the list of active asteroids.
     * Calls method to clear the active asteroids list of any destroyed asteroids.
     */
    private void spawnAsteroids() {
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        int minSpawnTime = 1000; // 1000ms = 1 second
        int maxSpawnTime = 3000; // 3000ms = 3 seconds

        // Runnable to spawn new asteroids every 1-3 seconds
        asteroidHandler.postDelayed(asteroidRunnable = new Runnable() {
            @Override
            public void run() {
                // Create a new asteroid and add it to the layout
                Asteroid newAsteroid = new Asteroid(asteroidLayout, screenWidth, screenHeight, player);
                asteroids.add(newAsteroid);

                //Iterates through Asteroid ArrayList removing any marked as being destroyed by
                //boolean characteristic in Asteroid class.
                for (int i = 0; i < asteroids.size(); i++) {
                    if (asteroids.get(i).isDestroyed()) {
                        asteroids.remove(i);
                        i--;
                    }
                }
                // Schedule the next asteroid spawn
                asteroidHandler.postDelayed(this,
                        random.nextInt(maxSpawnTime - minSpawnTime) + minSpawnTime); // Random interval (1 to 3 seconds)
            }
        }, random.nextInt(maxSpawnTime - minSpawnTime) + minSpawnTime); // Initial delay (1 to 3 seconds)
    }

    /**
     * Actively checks if any active bullets have entered any active asteroids hitbox. Uses a method from the asteroid class for
     * hit detection, and another to determine if the asteroid is already "destroyed" and just not removed from the list yet.
     * If the Asteroid is not already destroyed and the bullet is detected in the boundaries of the asteroid, the asteroid
     * takes damage from the player.
     */
    private void asteroidCollisionCheck(){
        for (int i = 0; i < playerBullets.size(); i++) {
            Bullet bullet = playerBullets.get(i);
            for (Asteroid asteroid : asteroids) {
                if (!asteroid.isDestroyed() && asteroid.isHit(bullet.getxPosition(), bullet.getyPosition())) {
                    asteroid.takeDamage(1);
                    bullet.stopBullet();
                    break;
                }
            }}
    }

    /**
     * Function to pause or resume animators controlling the list of current asteroids.
     * @param gameState : String representing "pause" or "resume"
     */
    private void handleAsteroidAnimation(String gameState){
        if(Objects.equals(gameState, "pause")){
            for (int i = 0; i < asteroids.size(); i++) {
                asteroids.get(i).pauseAsteroid();
            }
        } else if (Objects.equals(gameState, "resume")) {
            for (int i = 0; i < asteroids.size(); i++) {
                asteroids.get(i).resumeAsteroid();
            }
        }
    }

    public void startCountdownTimer() {
        isGameActive = false;
        pauseBtn.setEnabled(false);
         countdownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 == 0) {
                    countdownText.setText("Go!");
                } else {
                    countdownText.setText(String.valueOf(millisUntilFinished / 1000));
                }
                pauseGamePlay();
            }

            public void onFinish() {
                countdownText.setVisibility(View.GONE);
                countdownTint.setVisibility(View.GONE);
                pauseBtn.setEnabled(true);
                resumeGamePlay();
                isGameActive = true;
            }
        };countdownTimer.start();
    }
    /**
     * This method resumes gameplay when the user taps on the Resume button to unpause the game
     * Reactivates general game logic like player movement, enemy behavior, asteroid spawning
     * It's called when the game is in a valid game state to continue (e.g. after countdown or closing pause menu)
     */
    private void resumeGamePlay() {
        //Generate a sensor event listener for the application
        //The sensor manager uses the sensorEventListener to track any changes in the accelerometer
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        enemyHandler.post(enemyRunnable);
        enemy.startShooting();
//        player.startShooting();

        asteroidHandler.post(asteroidRunnable);
        handleAsteroidAnimation("resume");
    }

    /**
     * This method runs when the game screen becomes visible again (eg. after switching apps)
     * It doesn’t resume gameplay directly because the game might still need to stay paused
     * (eg. If the game was already paused before switching apps)
     * So don't put game resume code here but use resumeGamePlay() for that instead
     */
    protected void onResume() {
        super.onResume();
        if (!isPaused && isGameActive ) {
            resumeGamePlay();
        }
    }

    // This method handles the Android lifecycle and pause Logic
    private void pauseGamePlay() {
        mSensorManager.unregisterListener(sensorEventListener);
        enemyHandler.removeCallbacks(enemyRunnable);
        enemy.stopShooting();
//        player.stopShooting();
        asteroidHandler.removeCallbacks(asteroidRunnable);
        handleAsteroidAnimation("pause");
    }

    /**
     * Pauses all active game logic like sensor updates/ player movement, enemy behavior,
     * asteroid spawning
     */
    protected void onPause() {
        super.onPause();
        if (isPaused && !isGameActive ) {
            pauseGamePlay();
        }
    }

    /**
     * resumeGame and pauseGame are pause menu toggle functions that handle showing
     * and hiding the visual pause menu
     */
    public void resumeGame(){
        pauseMenu.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        homeButton.setVisibility(View.GONE);
        pauseMenuText.setVisibility(View.GONE);
        onResume();
        isPaused = false;
        isGameActive = true;
        resumeGamePlay();
    }
    public void pauseGame(){
        pauseMenu.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
        homeButton.setVisibility(View.VISIBLE);
        pauseMenuText.setVisibility(View.VISIBLE);
        onPause();
        isPaused = true;
        pauseGamePlay();
    }

    public void revertToMainMenu(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }

    /**
     * Create the enemy object, then sets up the bullets and death listener.
     * links the player to the enemy for possible tracking system, and begins
     * the automatic shooting. lastly does a death listener and ends game
     * @param enemySprite Image view of the enemy
     * @param gameLayout layout where the enemy will appear.
     */
    private void enemyCreate(ImageView enemySprite, ConstraintLayout gameLayout, ProgressBar healthBar){
        switch (difficulty){
            case "beginner":
                enemy = new Enemy(enemySprite,gameLayout,400f, -300f,
                        20f,100,2000, healthBar);
                break;

            case "medium":
                enemy = new Enemy(enemySprite,gameLayout, 400f, -300f,
                        30f,150,1000, healthBar);
                break;

            case "hard":
                enemy = new Enemy(enemySprite, gameLayout, 400f,  -300f,
                        50f, 200, 500, healthBar);
                break;
        }

        enemy.setPlayer(player);
        enemy.setBulletRegisterCallback(new Enemy.BulletRegisterCallback() {
            @Override
            public void enemyBullet(Bullet bullet) {
                enemyBullets.add(bullet);
            }
        });
        enemy.setEnemyDeathListener(new Enemy.EnemyDeathListener() {
            @Override
            public void enemyDeath() {
                updateStat("wins");
                endGame();

                Intent intent = new  Intent(GamePlay.this, EndGame.class);
                intent.putExtra("result", true);
                startActivity(intent);
                finish();
            }
        });
        enemy.startShooting();
    }

    /**
     * Start the enemy loop using a Runnable. It updates the movement, checks if it was hit.
     * and cleans up bullets that have been set off the screen of hit the player
     * loop is started with the enemyHandler
     */
    private void enemyRunnable(){
        enemyRunnable = new Runnable() {
            @Override
            public void run() {
                if(enemy != null){
                    enemy.updateEnemyPosition();
                    enemyHit(enemy,playerBullets);
                    playerHit(player,enemyBullets);
                    asteroidCollisionCheck();
                }

                cleanUpBullets(playerBullets);
                cleanUpBullets(enemyBullets);

                enemyHandler.postDelayed(this,17);
            }
        };
    }

    /**
     * Checks to see if a bullet from the player hit the enemy. iterates through a list of bullets
     * and if the bullet hit the enemy then it is removes it from the screen and takes health away
     * the enemy.
     * @param enemy the object that is used to check the position of the enemy ship
     * @param bullets list of bullets that are shot by the player
     */
    private void enemyHit(Enemy enemy, ArrayList<Bullet> bullets){
        Iterator<Bullet> iterator = bullets.iterator();
        while(iterator.hasNext()){
            Bullet bullet = iterator.next();
            if(bullet.hitTarget(enemy.getX(), enemy.getY(),enemy.getHeight(),enemy.getWidth())){
                enemy.gotHit(player.getFlameModeStatus());
                bullet.stopBullet();
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Initialize the player and links to bulletSpite
     * @param playerSprite Image view of the user sprite
     * @param bulletSprite Image view for the firing logic
     * @param playerHealthBar Image view of the player health
     */
    private void createPlayer(ImageView playerSprite, ImageView bulletSprite, ProgressBar playerHealthBar){
        bulletSprite.setVisibility(View.INVISIBLE);
        player = new Player(playerSprite,bulletSprite, playerHealthBar);
//        player.startShooting();
    }


    /**
     * Set up a listener for when the user taps the screen to handle shooting
     * Creates bullet image places it accordingly on the screen and adds it to
     * the gamelayout and logic
     * @param gameLayout the layout that detects the tap event
     */
    private void playerShooting(ConstraintLayout gameLayout){
        gameLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isGameActive && !isPaused) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastShotTime >= shootDelay){
                        lastShotTime = currentTime; //update the time of last shot

                        ImageView bullet = new ImageView(GamePlay.this);
                        bullet.setVisibility(View.INVISIBLE);

                        // bullets turn blue to indicate flame power-up has been activated
                        if(!player.getFlameModeStatus()){
                            bullet.setImageResource(R.drawable.laser_bullet);
                        }
                        else{
                            bullet.setImageResource(R.drawable.flame_bullet);
                        }


                        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                70, getResources().getDisplayMetrics());
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
                }
            }
        });
    }

    /**
     * listener to set up a event when the game ends. which is to start the EndGame class
     */
    private void playerDeathListener(){
        player.setPlayerDeathListener(new Player.PlayerDeathListener() {
            @Override
            public void playerDeath() {
                updateStat("deaths");
                endGame();

                Intent intent = new Intent(GamePlay.this, EndGame.class);
                intent.putExtra("result", false);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * checks to see if the enemies bullets have the player
     * which are added to a list and iterated through
     * If it has the player takes damage and removes the bullet from the screen
     * @param player object to check the collisions
     * @param bullets list of the enemy bullets
     */
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

    /**
     * Removes bullets from the list that aren't visible anymore by checking if its shown on screen
     * helps performance by clearing inactive objects
     * @param bulletList list of bullets to clean
     */
    private void cleanUpBullets(ArrayList<Bullet> bulletList){
        Iterator<Bullet> iterator = bulletList.iterator();
        while (iterator.hasNext()){
            Bullet bullet = iterator.next();
            if (!bullet.getSpriteView().isShown()){
                iterator.remove();
            }
        }
    }

    /**
     * This is called when one of the two players die
     * it stops the movement by removing call backs pausing and pausing animation
     */
    protected void endGame() {
        mSensorManager.unregisterListener(sensorEventListener);
        enemyHandler.removeCallbacks(enemyRunnable);

        asteroidHandler.removeCallbacks(asteroidRunnable);
        handleAsteroidAnimation("pause");
        // Pauses enemy shooting
        enemy.stopShooting();
//        player.stopShooting();
    }

    /**
     * From SharedPreferences gets the current value of the stat being updated and
     * re-saves an incremented value for that stat.
     * @param result : String that decides what stat is being updated (wins, deaths)
     */
    public void updateStat(String result){
        // Getting Player preferences for stats
        prefs = getSharedPreferences("PlayerPrefs", MODE_PRIVATE);
        int currentStat = prefs.getInt(result, 0); // Default to 0 if not found
        // Increment and save
        prefs.edit().putInt(result, currentStat + 1).apply();
    }
}
