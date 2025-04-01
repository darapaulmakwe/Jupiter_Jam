package com.jupiterjam;

import static com.jupiterjam.GameView.screenRatioX;
import static com.jupiterjam.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Rocket {
    int x, y, width, height, wingCounter =0;
    Bitmap flight1, flight2;

    Rocket (int screenY, Resources res){
        flight1 = BitmapFactory.decodeResource(res, R.drawable.jupiter_ship);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.jupiter_ship);

        width /= 4;
        height /= 4;

        width *= (int) screenRatioX;

        height *= (int) screenRatioY;

    }

    Bitmap getFlight(){
        if (wingCounter == 0){
            wingCounter++;
            return flight1;
        }

        wingCounter--;
        return flight2;
    }

}
