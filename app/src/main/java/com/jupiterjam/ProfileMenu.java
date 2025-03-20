package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_menu);
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();

    }

}
