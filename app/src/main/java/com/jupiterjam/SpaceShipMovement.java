package com.jupiterjam;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SpaceShipMovement {
    int x = 0, y = 0;
    Bitmap userShip;
    SpaceShipMovement(int screenX, int screenY, Resources res){
        userShip = BitmapFactory.decodeResource(res, R.drawable.jupiterlogo);
        userShip = Bitmap.createScaledBitmap(userShip, screenX, screenY, false);
    }
}
