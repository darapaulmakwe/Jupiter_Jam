package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

    }

    public void openPlayMenu(View myView){
        startActivity(new Intent(this,PlayMenu.class));
        finish();
    }
    public void openProfileMenu(View myView){
        startActivity(new Intent(this,ProfileMenu.class));
        finish();
    }

    public void openCredits(View myView){
        setContentView(R.layout.credits);

    }
    public void revert(View myView){
        setContentView(R.layout.main_menu);

    }
}