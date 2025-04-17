package com.jupiterjam;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.FrameLayout;

import java.util.Random;

public class Asteroid {

    private ImageView asteroidView; // The visual representation
    private ObjectAnimator animator;
    private FrameLayout asteroidLayout;
    private Context context;

    public int width = 225;
    public int height = 163; // Dimensions of the asteroid

    private int health;
    private boolean isDestroyed = false;

    private boolean isPowerUp;
    private int powerUp;

    public Asteroid(FrameLayout gameLayout, int screenWidth, int screenHeight) {

        this.asteroidLayout = gameLayout;
        this.context = gameLayout.getContext();

        // Set initial random position for the asteroid
        int startX = screenWidth; // Start off-screen (right side)
        int endX = -width;

        int minY = 600; // accounting for player and cpu area
        int maxY = screenHeight - minY;

        int startY = new Random().nextInt(maxY-minY+1) + minY;

        // Create the ImageView for the asteroid
        asteroidView = new ImageView(context);

        int chanceOfPowerUp = 10; // 10% chance to be a power-up asteroid
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
            health = 3;
        }
        else {
            asteroidView.setImageResource(R.drawable.plain_asteroid);
            health = 1;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        asteroidView.setLayoutParams(params);
        asteroidView.setX(startX);
        asteroidView.setY(startY);

        // Add to layout
        asteroidLayout.addView(asteroidView);

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

    public void takeDamage(int damage) {
        if (isDestroyed) return;

        health -= damage;
        if (health <= 0) {
            destroy(true); // destroyed by player
        }
    }

    // Hitbox detection to determine if the asteroid is hit.
    // Checks if bullet has entered the asteroids position.
    public boolean isHit(float x, float y) {

        float left = asteroidView.getX();
        float top = asteroidView.getY();
        float right = left + asteroidView.getWidth();
        float bottom = top + asteroidView.getHeight();

        return x >= left && x <= right && y >= top && y <= bottom;
    }

    /* Cancels asteroid animation and removes the image from layout. ie destroys the asteroid.
    ** Handles android explosion animation, and triggering a power up for the player if applicable
    */
    public void destroy(boolean wasKilledByPlayer){
        if (isDestroyed) return;

        isDestroyed = true;
        if (animator != null && animator.isRunning()) animator.cancel();

        explode();

        if (wasKilledByPlayer && isPowerUp) {
            triggerPowerUp(powerUp);
        }
    }
    // Display explosion and remove the view after a delay
    private void explode(){
        int asteroidRemoveDelay = 1000;
        asteroidView.setImageResource(R.drawable.explosion);
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

    // Functionality of each power up to be applied.
    private void triggerPowerUp(int powerUp) {
        switch(powerUp){
            case 0:
                // TODO: ability function for bullet asteroid
            case 1:
                // TODO: ability function for flame asteroid
            case 2:
                // TODO: ability function for health asteroid
        }
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public ImageView getAsteroidView() {
        return asteroidView;
    }
}
