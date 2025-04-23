package com.jupiterjam;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;


/**
 * Bullet class, used for handling the movement, collision detection
 * and visibility.
 */

public class Bullet {
    private ImageView spriteView; // The visual representation
    private float xPosition;
    private float yPosition;
    private boolean isBulletActive = false;
    private float bulletSpeed = 20;
    private final int frameRate = 60;
    private final Handler handler = new Handler(); // Handler to manage bullet movement timing

    //Constructor

    /**
     * Constructor for Bullet object, sets speed and visibility
     * @param spriteView image view representing the bullet
     * @param xPosition the starting x position of the bullet
     * @param yPosition the starting y position of the bullet
     * @param bulletSpeed speed of the bullet
     */
    public Bullet(ImageView spriteView, float xPosition, float yPosition, float bulletSpeed){
        this.spriteView = spriteView;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.bulletSpeed = bulletSpeed;
        isBulletActive = true;
    }

    /**
     * Starts the bullets movement. it posts a timed loop
     * moves the bullet every frame based on the bullet speed and checks
     * to see if the bullet is off screen
     */
    public void startMovement(){
        int positionCorrection = 100;  // optional
        spriteView.setX(xPosition + positionCorrection);
        spriteView.setY(yPosition);
        System.out.println("X Position: " +xPosition + "Y Position: " + yPosition);
        spriteView.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBulletActive) {
                    // Update the bullet's Y position
                    yPosition += bulletSpeed;
                    spriteView.setY(yPosition);

                    // Check for conditions to stop the bullet (e.g., hitting a target, going off-screen)
                    if (yPosition < 0 || yPosition > 5000) { // Example: Bullet went off the top screen
                        stopBullet();
                        return;
                    }


                    // Schedule the next update
                    handler.postDelayed(this, 1000 / frameRate);
                }
            }
        }, 1000 / frameRate); // Initial delay
    }

    /**
     * Stops the bullets movement and hides it.
     * Used for when a bullet goes off the screen or hits a target
     */
    public void stopBullet(){
        isBulletActive = false;
        spriteView.setVisibility(ImageView.GONE);
    }

    /**
     * Checks to see if a bullet hits the target
     * @param positionX   X position of the target
     * @param positionY   Y position of the target
     * @param targetHeight Height of the target
     * @param targetWidth Width of the target
     * @return it will return true if the bullet is overlapping with the target
     */
    public boolean hitTarget(float positionX, float positionY, float targetHeight, float targetWidth){
        float bulletX = spriteView.getX();
        float bulletY = spriteView.getY();
        float bulletHeight = spriteView.getHeight();
        float bulletWidth = spriteView.getWidth();

        return bulletX < positionX + targetWidth && // checks to see if the bullets lhs is less than targets rhs
               bulletX + bulletWidth > positionX && // bullets rhs is greater then targets lhs
                bulletY < positionY + targetHeight &&// bullets top is above targets bottom
                bulletY + bulletHeight > positionY;// bullets bottom is below the targets top

    }

    // getters methods for the bullet and sprite
    public float getxPosition(){ return xPosition; }
    public float getyPosition(){ return yPosition; }

    public ImageView getSpriteView(){
        return spriteView;
    }
}

