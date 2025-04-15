package com.jupiterjam;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

public class Bullet {
    private ImageView spriteView; // The visual representation
    private float xPosition;
    private float yPosition;
    private boolean isBulletActive = false;
    private float bulletSpeed = 20;
    private final int frameRate = 60;
    private final Handler handler = new Handler();

    //Constructor
    public Bullet(ImageView spriteView, float xPosition, float yPosition, float bulletSpeed){
        this.spriteView = spriteView;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.bulletSpeed = bulletSpeed;
        isBulletActive = true;
    }
    public void startMovement(){
        spriteView.setX(xPosition);
        spriteView.setY(yPosition);
        spriteView.setVisibility(View.VISIBLE);
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

*/