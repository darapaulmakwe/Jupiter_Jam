package com.jupiterjam;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Player {
    private int health = 100;
    private final int maxHealth = 100;
    private ImageView spriteView; // The visual representation
    private ImageView bulletView; //The visual representation of the bullet
    private boolean isShooting = false;
    private ProgressBar healthBar;
    // Movement parameters
    private float tiltSensitivityX = 30f;
    private float maxHorizontalDistance = 300f;
    private float horizontalMoveThreshold = 0.3f;

    // Smoothing parameters
    private float smoothedX = 0f;
    private boolean hasSmoothedXInitialized = false;
    private static final float ALPHA = 0.15f; // Smoothing factor

    public float getX() { return spriteView.getX();
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

    private PlayerDeathListener deathListener;

    private boolean isFlameMode = false;
    private boolean isBulletBoosted = false;

    public interface PlayerDeathListener{
        void playerDeath();
    }

    public void setPlayerDeathListener(PlayerDeathListener listener){
        this.deathListener = listener;
    }

    // Constructor
    public Player(ImageView spriteView, ImageView bulletView, ProgressBar playerHealthBar) {
        this.spriteView = spriteView;
        this.bulletView = bulletView;
        // Initialize smoothing flag (or could be done in a reset method)
        this.hasSmoothedXInitialized = false;

        healthBar = playerHealthBar;
        initializeHealthBar();
    }

    /**
     * Updates the player's horizontal position based on sensor input.
     * This method now also handles smoothing internally.
     * @param rawXAcceleration The raw acceleration value from the sensor's X-axis.
     */
    public void updatePosition(float rawXAcceleration) {
        // Apply Smoothing internally
        if (!this.hasSmoothedXInitialized) {
            this.smoothedX = rawXAcceleration;
            this.hasSmoothedXInitialized = true;
        } else {
            this.smoothedX = ALPHA * rawXAcceleration + (1.0f - ALPHA) * this.smoothedX;
        }

        // Use the smoothed value for calculations
        float xControlValue = this.smoothedX;

        float horizontalTranslation = 0f; // Default to no movement

        // Check if the tilt (absolute acceleration value) is significant enough
        if (Math.abs(xControlValue) > horizontalMoveThreshold) {

            horizontalTranslation = -xControlValue * tiltSensitivityX;

            // Ensures the rocket doesn't go past set horizontal boundaries
            horizontalTranslation = Math.max(-maxHorizontalDistance, Math.min(maxHorizontalDistance, horizontalTranslation));

        } else {
            // Snap back to center if below threshold
            horizontalTranslation = 0f;
        }

        // Apply translation to the Player's ImageView
        this.spriteView.setTranslationX(horizontalTranslation);
    }
    public Bullet shoot(){
        float xPos = spriteView.getX();
        float yPos = spriteView.getY();

        Bullet bullet;
        // bullet speed determined by whether a power-up is active
        if(isBulletBoosted){
            bullet = new Bullet(bulletView, xPos, yPos, -50);
        }
        else{
            bullet = new Bullet(bulletView, xPos, yPos, -20);
        }
        bullet.startMovement();
        return bullet;
    }
    private void spawnBullet(float xPos, float yPos){
    }
    /**
     * Resets the initialization flag for smoothing.
     */
    public void resetSmoothing() {
        this.hasSmoothedXInitialized = false;
        smoothedX = 0f;
    }

    //Getters/Setters
    public void setTiltSensitivityX(float sensitivity) {
        this.tiltSensitivityX = sensitivity;
    }

    public void setMaxHorizontalDistance(float distance) {
        this.maxHorizontalDistance = distance;
    }

    public void setBulletView(ImageView bulletView){
        this.bulletView = bulletView;
    }

    public void gotHit(){
        health -= 5;
        healthBar.setProgress(health);
        updateHealthBarColor();
        if(health <= 0){
            spriteView.setVisibility(View.INVISIBLE);
            if(deathListener != null){
                deathListener.playerDeath();
            }
        }
    }
    private void initializeHealthBar(){
        healthBar.setMax(maxHealth);
        healthBar.setProgress(health);
        updateHealthBarColor();
    }

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
    /**
     *  Function to handle power-up from a health asteroid.
     *  Heals the player for 30 and gives visual feedback on the healthbar
     *  that the power-up activates.
     */
    public void heal(){
        health += 30;
        if (health > maxHealth) health = maxHealth;

        // Apply highlight to show activation
        healthBar.setBackgroundResource(R.drawable.healthbar_highlight);
        // Remove it after a short delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                healthBar.setBackgroundResource(0); // Remove background
            }
        }, 1500);

        healthBar.setProgress(health);
        updateHealthBarColor();
    }

    public void activateFlameMode(){
        Handler flameHandler = new Handler();
        int flameDuration = 5000; // 5 seconds of activation
        if(!isFlameMode){
            isFlameMode = true;

            flameHandler.postDelayed(() -> isFlameMode = false, flameDuration);
        }
    }

    public boolean getFlameModeStatus(){
        return isFlameMode;
    }

    public void activateBulletBoost() {
        Handler boostHandler = new Handler();
        int boostDuration = 5000; // 5 seconds
        if (!isBulletBoosted) {
            isBulletBoosted = true;
            boostHandler.postDelayed(() -> isFlameMode = false, boostDuration);
        }
    }
}
