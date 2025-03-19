package com.jupiterjam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Mainmenu extends AppCompatActivity  {

    public void openPlayMenu(View myView){
        Button playButton = (Button) myView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);
    }
}
