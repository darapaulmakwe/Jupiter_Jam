package com.jupiterjam;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
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
        int startY = new Random().nextInt(screenHeight - height) + height;

        // Create the ImageView for the asteroid
        asteroidView = new ImageView(context);

        // 10% chance of being a special asteroid
        isPowerUp = new Random().nextInt(10) == 0;

        // Set image and health based on type
        if (isPowerUp) {
            // Random int to determine what power up spawns. Even chance between all power-up types
            powerUp = new Random().nextInt(3);
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
            health = 5;
        }
        else {
            asteroidView.setImageResource(R.drawable.plain_asteroid);
            health = new Random().nextInt(3) + 1; // Health 1 to 3
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        asteroidView.setLayoutParams(params);
        asteroidView.setX(startX);
        asteroidView.setY(startY);

        // Add to layout
        asteroidLayout.addView(asteroidView);

        // Random speed: 5 to 10 seconds to cross the screen
        int duration = new Random().nextInt(5000) + 5000;

        // Animate X position from right to left
        animator = ObjectAnimator.ofFloat(asteroidView, "x", startX, endX);
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

        //explode(); TODO: implement explosion after asteroid is destroyed
        asteroidLayout.removeView(asteroidView);

        if (wasKilledByPlayer && isPowerUp) {
            triggerPowerUp(powerUp);
        }
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
