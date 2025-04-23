package com.jupiterjam;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Random;


/**
 * Enemy class that handles the movement, shooting bullets, taking damage
 * tracking the player, and creating a health bar.
 */
public class Enemy {
    private int health = 100;
    private final int maxHealth = 100;
    private ImageView spriteView;
    private ImageView bulletView;
    private ViewGroup parentLayout;
    private ProgressBar healthBar;
    // for movement speed directions etc
    private float movementSpeed = 20f;
    private float maxHorizontal = 600f;
    private float direction = 1f;
    private float minHorizontal = 0f;


    // for random movement
    private long lastDirectionChange = 0;
    private long timeUntilNextChange = 1500;
    private Random random = new Random();
    // tracking settings
    private long lastTimeTracked = 0;
    private long timeUntilNextTrack = 3000;
    private Player player;

    /**
     * reference for tracking player
     * @param player object to track
     */
    public void setPlayer(Player player){
        this.player = player;
    }

    // for bullets
    private Handler bulletHandler = new Handler();
    private int timeBetweenShots = 1000;
    boolean isShooting = false;
    private ArrayList<Bullet> enemyBullets = new ArrayList<>();


    // easier and faster access to dimensions of ship and position
    public float getX(){
        return spriteView.getX();
    }
    public float getY(){
        return spriteView.getY();
    }
    public float getHeight(){
        return spriteView.getHeight();
    }
    public float getWidth(){
        return spriteView.getWidth();
    }
// for death listener, sends to the gameplay.java
    private EnemyDeathListener deathListener;
    public interface EnemyDeathListener{
        void enemyDeath();
    }

    /**
     * Sets a listener that is triggered when the enemy dies
     * @param listener  the callback that executed on the death
     */
    public void setEnemyDeathListener(EnemyDeathListener listener){
        this.deathListener = listener;
    }

    // tracks for fired bullets
    public interface BulletRegisterCallback{
        void enemyBullet(Bullet bullet);
    }
    private BulletRegisterCallback bulletRegisterCallback;

    /**
     * Constructor off the enemy with its attributes and initializes the health bar
     * @param spriteView visual of the enemy
     * @param bulletView layout for bullet to be added to
     * @param maxHorizontal the max movement to the right
     * @param minHorizontal the max movement to the left
     * @param movementSpeed speed that the enemy movement
     * @param health players amount of health
     * @param timeBetweenShots amount of time between each shot taken
     * @param enemyHealthBar progress bar showing enemy health
     */

    public Enemy(ImageView spriteView, ViewGroup bulletView, float maxHorizontal, float minHorizontal,
                 float movementSpeed, int health, int timeBetweenShots, ProgressBar enemyHealthBar){
        this.spriteView = spriteView;
        this.maxHorizontal = maxHorizontal;
        this.minHorizontal = minHorizontal;
        this.parentLayout = bulletView;
        this.movementSpeed = movementSpeed;
        this.health = health;
        this.timeBetweenShots = timeBetweenShots;
        healthBar = enemyHealthBar;
        initializeHealthBar();
    }

    /**
     * sets a callback for tracking the bullets the enemy fired
     * @param callback receives the bullet reference
     */
    public void setBulletRegisterCallback(BulletRegisterCallback callback){
        this.bulletRegisterCallback = callback;
    }

    /**
     * Called every so often to update and move the enemy
     */

    public void updateEnemyPosition(){
        move();
        }

// sources i used to help with tracking the player
//https://www.baeldung.com/java-ternary-operator, and https://www.w3schools.com/java/java_conditions_shorthand.asp

    /**
     * Movement of the enemy, used random direction changes
     * and tracks the player once and a while
     */
    private void move() {
        long currentTime =  System.currentTimeMillis();
        if((currentTime - lastDirectionChange) > timeUntilNextChange){
            if(random.nextFloat() < 0.33){
                direction *= -1; // 33% change of random movement back and forth
            }
            timeUntilNextChange = 500 + random.nextInt(800); // next change 0.5-2 seconds
            lastDirectionChange = currentTime;// iterate time


        }


        // tracking
        if((currentTime - lastTimeTracked ) > timeUntilNextTrack && player!= null){
            float enemyX = spriteView.getTranslationX(); // get the direction of the enemy
            float playerX = player.getX();
            float directionToPlayer = playerX - enemyX; // if the enemy is to the right result is pos if to the left then neg


            if(Math.abs(directionToPlayer) > 2){
                direction = directionToPlayer > 0 ? 1: -1;
            }
            lastTimeTracked = currentTime;
            timeUntilNextTrack = 500 + random.nextInt(800);
        }

        // movement and limits
        float currentX = spriteView.getTranslationX();
        float newX = currentX + (direction * movementSpeed);

        if (newX >= maxHorizontal){
            newX = maxHorizontal;
            direction *= -1;
        }
        if (newX <= minHorizontal){
            newX = minHorizontal;
            direction *= -1;
        }
        spriteView.setTranslationX(newX);

    }

    /**
     * Starts the shooting and sets bullet creation
     */
    public void startShooting(){
        if(!isShooting){
            isShooting = true;
            bulletHandler.post(startBullet);
        }

    }

    /**
     * loop that shoots the bullets
     */
    private Runnable startBullet = new Runnable() {
        @Override
        public void run() {
            if (isShooting) {
                Bullet bullet = shoot();
                bulletHandler.postDelayed(this, timeBetweenShots);
            }
        }

    };

    /**
     * stops shooting and stops creation
     */
    public void stopShooting(){
        isShooting=false;
        bulletHandler.removeCallbacks(startBullet);
    }

    /**
     * Fires bullets and registers it
     * @return object that was fired
     */
    private Bullet shoot(){
        float xPos = spriteView.getX();
        float yPos = spriteView.getY() + spriteView.getHeight();

        ImageView bulletView = new ImageView(spriteView.getContext());
        bulletView.setImageResource(R.drawable.laser_bullet);
        bulletView.setVisibility(View.INVISIBLE);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70 , spriteView.getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
        bulletView.setLayoutParams(layoutParams);
        parentLayout.addView(bulletView);


        Bullet bullet = new Bullet(bulletView, xPos, yPos,30);
        bullet.startMovement();

        if(bulletRegisterCallback != null){
            bulletRegisterCallback.enemyBullet(bullet);

        }
        return bullet;


    }

    /**
     * Called when enemy is hit. Applies damage and checks for death
     * @param isFlameMode power up that if true takes additional damage
     */
    public void gotHit(boolean isFlameMode){
        // more damage taken as a result of the damage boost from flame mode
        if(isFlameMode){
            health -= 20;
        }
        else{
            health -= 10;
        }


        healthBar.setProgress(health);
        updateHealthBarColor();
        if(health <= 0){
            spriteView.setVisibility(View.INVISIBLE);
            isShooting = false;
            if(deathListener != null){
                deathListener.enemyDeath();
            }
        }

    }

    /**
     * Health bar and current health and sets color
     */
    private void initializeHealthBar(){
        healthBar.setMax(maxHealth);
        healthBar.setProgress(health);
        updateHealthBarColor();
    }

    /**
     * updates the color bar based on the health percentage
     */

    private void updateHealthBarColor() {
        int percentage = (health * 100) / maxHealth;

        int color;
        if (percentage > 60) {
            color = Color.GREEN;
        } else if (percentage > 30) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }

        // Apply color to progress drawable
        healthBar.setProgressTintList(ColorStateList.valueOf(color));
    }
}
