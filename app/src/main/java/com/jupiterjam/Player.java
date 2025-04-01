package com.jupiterjam;

import static com.jupiterjam.GameView.screenRatioX;
import static com.jupiterjam.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Player {
    int x, y, widthOriginal, heightOriginal, width, height, wingCounter =0;
    Bitmap flight1, flight2;

    Player (int screenY, Resources res){
        float scaledWidth = widthOriginal/ 4f * screenRatioX;
        float scaledHeight = heightOriginal / 4f * screenRatioY;

        width = Math.max(1, Math.round(scaledWidth));
        height = Math.max(1, Math.round(scaledHeight));

        flight1 = BitmapFactory.decodeResource(res, R.drawable.jupiter_ship);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.jupiter_ship);



        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        y = screenY / 2;
        x = (int) (64 * screenRatioX);
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
