package com.jupiterjam;

import android.media.Image;
import android.widget.ImageView;

public class Enemy {
    private int health = 100;

    private ImageView spriteView;
    private float movementSpeed = 50f; // edit this aswell

    private float maxHorizontal = 600f;
    private float direction = 1f;
    private float minHorizontal = 0f;

    public Enemy(ImageView spriteView, float maxHorizontal, float minHorizontal){
        this.spriteView = spriteView;
        this.maxHorizontal = maxHorizontal;
        this.minHorizontal = minHorizontal;
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
}
