package com.jupiterjam;
import android.os.Handler;
import android.widget.ImageView;

public class Bullet {
    private ImageView spriteView; // The visual representation
    private float xPosition;
    private float yPosition;
    private boolean isBulletActive = false;
    private final float bulletSpeed = -20;
    private final int frameRate = 60;
    private final Handler handler = new Handler();

    //Constructor
    public Bullet(ImageView spriteView, float xPosition, float yPosition){
        this.spriteView = spriteView;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        spriteView.setVisibility(ImageView.VISIBLE);
        isBulletActive = true;
    }
    public void startMovement(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isBulletActive) {
                    // Update the bullet's Y position
                    yPosition += bulletSpeed;
                    spriteView.setY(yPosition);

                    // Check for conditions to stop the bullet (e.g., hitting a target, going off-screen)
                    if (yPosition < 0) { // Example: Bullet went off the top screen
                        stopBullet();
                        return;
                    }

                    // Schedule the next update
                    handler.postDelayed(this, 1000 / frameRate);
                }
            }
        }, 1000 / frameRate); // Initial delay
    }
    public void stopBullet(){
        isBulletActive = false;
        spriteView.setVisibility(ImageView.GONE);
    }
}

//For player
/*
public void shoot(){
        // Initialize bullet sprite
        ImageView bulletSprite = findViewById(R.id.bullet);
        float xPosition = this.spriteView.getX();
        float yPosition = this.spriteView.getY();
        Bullet bullet = new Bullet(bulletSprite, xPosition, yPosition);
        bullet.startMovement();
    }
*/