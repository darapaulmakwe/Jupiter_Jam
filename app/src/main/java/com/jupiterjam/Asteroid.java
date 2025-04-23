package com.jupiterjam;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.FrameLayout;

import java.util.Random;

public class Asteroid {

    private ImageView asteroidView; // The visual representation
    private ObjectAnimator animator;
    private FrameLayout asteroidLayout; // Layout to display Arraylist of active asteroids
    private Context context;

    // Dimensions of the asteroid
    public int width = 225;
    public int height = 163;

    private int health;
    private boolean isDestroyed = false;

    private boolean isPowerUp;
    private int powerUp;

    private Player player;

    /**
     * Constructor for asteroid object.
     * @param gameLayout : frame layout for asteroid to be applied to
     * @param startX : screenWidth of device to be used for animation start point and asteroid
     *                 frameLayout parameters
     * @param screenHeight : screen height of device to be used for frameLayout parameters
     * @param player : player initialized in gamePlay class used to apply power-ups to the player mid-game
     */
    public Asteroid(FrameLayout gameLayout, int startX, int screenHeight,Player player) {

        this.asteroidLayout = gameLayout;
        this.context = gameLayout.getContext();
        this.player = player;

        createAsteroid();

        setAsteroidLayout(startX, screenHeight);

        animateAsteroid(startX);
    }

    /**
     *  Creates ImageView for the asteroid, determines if it is a power-up or not and
     *  sets the asteroid image and health accordingly.
     *
     *  Whether an asteroid is a power-up or not is determined by a randomly generated number
     *  and the odds in which an asteroid has to be a power-up.
     */
    private void createAsteroid(){
        // Create the ImageView for the asteroid
        asteroidView = new ImageView(context);

        int chanceOfPowerUp = 7; // 1/7 chance to be a power-up asteroid
        isPowerUp = new Random().nextInt(chanceOfPowerUp) == 0;

        // Set image and health based on type
        if (isPowerUp) {
            int numberOfPowerUps = 3;
            // Random int to determine what power up spawns. Even chance between all power-up types
            powerUp = new Random().nextInt(numberOfPowerUps);
            switch(powerUp) {
                case 0:
                    asteroidView.setImageResource(R.drawable.bullet_asteroid);
                    break;
                case 1:
                    asteroidView.setImageResource(R.drawable.flame_asteroid);
                    break;
                case 2:
                    asteroidView.setImageResource(R.drawable.health_asteroid);
                    break;
            }
        }
        else {
            asteroidView.setImageResource(R.drawable.plain_asteroid);
        }
        health = 1;
    }

    /**
     * Sets up asteroid ImageView and where it sits on the FrameLayout.
     * @param startX : x position of Asteroid ImageView on FrameLayout.
     * @param screenHeight : Height of the screen used to determine constraints of what y values the asteroid
     *                       can spawn at.
     */
    private void setAsteroidLayout(int startX, int screenHeight){
        int minY = 600; // accounting for player and cpu area
        int maxY = screenHeight - minY;
        int startY = new Random().nextInt(maxY-minY+1) + minY;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        asteroidView.setLayoutParams(params);
        asteroidView.setX(startX);
        asteroidView.setY(startY);

        // Add to layout
        asteroidLayout.addView(asteroidView);
    }

    /**
     * Method animates the Asteroid horizontally across the screen from right to left, for a random duration in a bound.
     * Adds Rotation animation and handles all situations the animation can run into.
     * @param startX
     */
    private void animateAsteroid(int startX) {
        int endX = -width; // end at left side of screen
        int minDuration = 5000;
        int maxDuration = 12000;
        // Random speed: 5 to 12 seconds to cross the screen
        int duration = new Random().nextInt(minDuration) + (maxDuration - minDuration);

        // Animate X position from right to left with rotation
        animator = ObjectAnimator.ofFloat(asteroidView, "x", startX, endX);
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(asteroidView, "rotation", 0f, 360f);
        rotateAnim.setDuration(duration);
        rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnim.start();
        animator.setDuration(duration);

        // When animation ends, remove the asteroid
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                destroy(false); // natural destruction (off screen)
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                destroy(false); // canceled = also remove
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        animator.start();
    }

    /**
     * Removes a given int amount from the Asteroids health. If the damage is more than the remaining
     * health asteroid destruction is triggered.
     * @param damage : int amount of damage to take.
     */
    public void takeDamage(int damage) {
        if (isDestroyed) return;

        health -= damage;
        if (health <= 0) {
            destroy(true); // destroyed by player
        }
    }

    /**
     * Hitbox detection to determine if the asteroid is hit.
     * Checks if an object has entered the asteroids position using the boundaries
     * of the asteroids ImageView and the objects x and y position.
     * @param x : x position of object hitting asteroid
     * @param y : y position of object hitting asteroid
     */
    public boolean isHit(float x, float y) {

        float left = asteroidView.getX();
        float top = asteroidView.getY();
        float right = left + asteroidView.getWidth();
        float bottom = top + asteroidView.getHeight();

        return x >= left && x <= right && y >= top && y <= bottom;
    }

    /**
     * Cancels asteroid animation and removes the image from layout. ie destroys the asteroid.
     * Handles triggering asteroid explosion animation, and triggering a power up for the player
     * if applicable
     */
    public void destroy(boolean wasKilledByPlayer){
        if (isDestroyed) return; // triggers asteroid removal in GamePlay class

        isDestroyed = true;
        if (animator != null && animator.isRunning()) animator.cancel();

        explode(); // includes removal from layout

        if (wasKilledByPlayer && isPowerUp) {
            triggerPowerUp(powerUp);
        }
    }

    /**
     * Display an explosion image in place of the asteroid being destroyed and remove the view
     * after a delay.
     */
    private void explode(){
        int asteroidRemoveDelay = 1000;
        asteroidView.setImageResource(R.drawable.explosion);
        MediaPlayer explosionSound = MediaPlayer.create(context, R.raw.explosion_sound);
        explosionSound.setOnCompletionListener(mp -> {
            mp.release(); // clean up resources
        });
        explosionSound.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                asteroidLayout.removeView(asteroidView);
            }
        }, asteroidRemoveDelay);
    }

    public void pauseAsteroid(){
        animator.pause();
    }
    public void resumeAsteroid(){
        animator.resume();
    }

    /**
     * Calls corresponding Player method to activate a power-up.
     * @param powerUp : random number generated when the asteroid was created.
     */
    private void triggerPowerUp(int powerUp) {
        switch(powerUp){
            case 0:
                // speeds bullets up
                player.activateBulletBoost();
                break;
            case 1:
                // gives player a damage boost
                player.activateFlameMode();
                break;
            case 2:
                // heals player for 30
                player.heal();
                break;
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }
}
