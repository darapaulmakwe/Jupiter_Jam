package com.jupiterjam;
import android.view.View;
import android.widget.ImageView;
public class Player {
    private int health = 100;
    private ImageView spriteView; // The visual representation
    private ImageView bulletView; //The visual representation of the bullet

    // Movement parameters
    private float tiltSensitivityX = 30f;
    private float maxHorizontalDistance = 300f;
    private float horizontalMoveThreshold = 0.3f;

    // Smoothing parameters
    private float smoothedX = 0f;
    private boolean hasSmoothedXInitialized = false;
    private static final float ALPHA = 0.15f; // Smoothing factor

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

    private PlayerDeathListener deathListener;
    public interface PlayerDeathListener{
        void playerDeath();
    }

    public void setPlayerDeathListener(PlayerDeathListener listener){
        this.deathListener = listener;
    }

    // Constructor
    public Player(ImageView spriteView, ImageView bulletView) {
        this.spriteView = spriteView;
        this.bulletView = bulletView;
        // Initialize smoothing flag (or could be done in a reset method)
        this.hasSmoothedXInitialized = false;
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

        Bullet bullet = new Bullet(bulletView, xPos, yPos,-20);
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
        health -= 10;
        if(health <= 0){
            spriteView.setVisibility(View.INVISIBLE);
            if(deathListener != null){
                deathListener.playerDeath();
            }
        }


    }
}
