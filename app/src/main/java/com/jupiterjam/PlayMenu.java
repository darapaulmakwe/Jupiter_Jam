package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PlayMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_menu);

    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();

    }

    public void openBeginnerTab(View myView){
        gameDifficulty("beginner");
    }
    public void openMediumTab(View myView){
        gameDifficulty("medium");
    }
    public void openHardTab(View myView){
        gameDifficulty("hard");
    }

    private void gameDifficulty(String difficulty){
        Intent intent = new Intent(this, GamePlay.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        finish();
    }

}
