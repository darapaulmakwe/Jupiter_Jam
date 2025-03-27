package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileMenu extends AppCompatActivity {

    EditText userName;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        userName = findViewById(R.id.usernameInput);

        setContentView(R.layout.profile_menu);
    }

    Customization userPreferences = new Customization("","jupiter","jupiter_ship.png");

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }

    public void changeUserName(View myView){
        userPreferences.setName(userName.getText().toString());
    }

    public void openThemeMenu(View myView){
        startActivity(new Intent(this,Customization.class));
        finish();
    }

    /*public int getWins(Player user){
        return user.wins;
    }

    public int getLoses(Player user){
        return user.loses;
    }*/
}
