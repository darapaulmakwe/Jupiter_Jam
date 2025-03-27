package com.jupiterjam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Customization extends AppCompatActivity {

    TextView currentTheme;
    public String userName;
    public String themeName;
    public String themeImage;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        currentTheme = findViewById(R.id.currentTheme);

        setContentView(R.layout.theme_menu);
    }

    public Customization(String userName, String themeName, String themeImage){
        this.userName = userName;
        this.themeName = themeName;
        this.themeImage = themeImage;
    }
    public void setName(String name){
        this.userName = name;
    }

    public String getName(){
        return this.userName;
    }

    public void setTheme(String theme){
        this.themeName = theme;
        this.themeImage = theme +"_ship.png";
    }

    public String getThemeName(){
        return this.themeName;
    }

    public String getThemeImage(){
        return this.themeImage;
    }
    //    public void changeTheme(View myView){
//
//        if (myView.getId() == R.id.themeButton1) {
//            userPreferences.setTheme("jupiter");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//        else if (myView.getId() == R.id.themeButton2) {
//            userPreferences.setTheme("earth");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//        else if (myView.getId() == R.id.themeButton3) {
//            userPreferences.setTheme("mars");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//        else if (myView.getId() == R.id.themeButton4) {
//            userPreferences.setTheme("neptune");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//        else if (myView.getId() == R.id.themeButton5) {
//            userPreferences.setTheme("saturn");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//        else if (myView.getId() == R.id.themeButton6) {
//            userPreferences.setTheme("moon");
//            currentTheme.setText(userPreferences.getThemeName());
//        }
//    }
    public void backToProfile(View myView) {
        startActivity(new Intent(this,ProfileMenu.class));
        finish();
    }

}
