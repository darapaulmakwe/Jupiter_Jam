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
        startActivity(new Intent(this,GamePlay.class));
        finish();
    }

}
