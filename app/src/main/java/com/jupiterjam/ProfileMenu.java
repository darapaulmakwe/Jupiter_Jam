package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileMenu extends AppCompatActivity {

    EditText userName;
    TextView currentTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_menu);

        userName = findViewById(R.id.usernameInput);
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
        setContentView(R.layout.theme_menu);

        currentTheme = findViewById(R.id.currentTheme);

    }

    public void backToProfile(View myView) {
        setContentView(R.layout.profile_menu);
    }

    //TODO: enumerate buttons and use switch statement
    public void changeTheme(View myView){

        if (myView.getId() == R.id.themeButton1) {
            userPreferences.setTheme("jupiter");
            currentTheme.setText(userPreferences.getThemeName());
        }
        else if (myView.getId() == R.id.themeButton2) {
            userPreferences.setTheme("earth");
            currentTheme.setText(userPreferences.getThemeName());
        }
        else if (myView.getId() == R.id.themeButton3) {
            userPreferences.setTheme("mars");
            currentTheme.setText(userPreferences.getThemeName());
        }
        else if (myView.getId() == R.id.themeButton4) {
            userPreferences.setTheme("neptune");
            currentTheme.setText(userPreferences.getThemeName());
        }
        else if (myView.getId() == R.id.themeButton5) {
            userPreferences.setTheme("saturn");
            currentTheme.setText(userPreferences.getThemeName());
        }
        else if (myView.getId() == R.id.themeButton6) {
            userPreferences.setTheme("moon");
            currentTheme.setText(userPreferences.getThemeName());
        }
    }
    /*public int getWins(Player user){
        return user.wins;
    }

    public int getLoses(Player user){
        return user.loses;
    }*/
}
