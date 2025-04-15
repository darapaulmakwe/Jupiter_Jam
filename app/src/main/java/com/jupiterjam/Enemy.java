package com.jupiterjam;

import android.media.Image;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class Enemy {
    private int health = 100;

    private ImageView spriteView;
    private ImageView bulletView;
    private ViewGroup parentLayout;
    private float movementSpeed = 50f; // edit this aswell

    private float maxHorizontal = 600f;
    private float direction = 1f;
    private float minHorizontal = 0f;
    private Handler bulletHandler = new Handler();
    private final int timeBetweenShots = 1000;
    boolean isShooting = false;

    public Enemy(ImageView spriteView, ViewGroup bulletView, float maxHorizontal, float minHorizontal){
        this.spriteView = spriteView;
        this.maxHorizontal = maxHorizontal;
        this.minHorizontal = minHorizontal;
        this.parentLayout = bulletView;

    }

    public void updateEnemyPosition(){
        move();
        }


    private void move() {
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

    public void startShooting(){
        if(!isShooting){
            isShooting = true;
            bulletHandler.post(startBullet);
        }

    }

    private Runnable startBullet = new Runnable() {
        @Override
        public void run() {
            if (isShooting) {
                shoot();
                bulletHandler.postDelayed(this, timeBetweenShots);
            }
        }
    };
    public void stopShooting(){
        isShooting=false;
        bulletHandler.removeCallbacks(startBullet);
    }


    private void shoot(){
        float xPos = spriteView.getX();
        float yPos = spriteView.getY() + spriteView.getHeight();

        ImageView bulletView = new ImageView(spriteView.getContext());
        bulletView.setImageResource(R.drawable.laser_bullet);
        bulletView.setVisibility(View.INVISIBLE);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100 , spriteView.getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
        bulletView.setLayoutParams(layoutParams);
        parentLayout.addView(bulletView);


        Bullet bullet = new Bullet(bulletView, xPos, yPos,20);
        bullet.startMovement();

    }
}
