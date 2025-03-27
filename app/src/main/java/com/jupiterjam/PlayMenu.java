package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class PlayMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_menu);

        findViewById(R.id.beginnerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                startActivity(new Intent(PlayMenu.this, GamePlay.class));
            }
        });
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();

    }

}
