package com.jupiterjam;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Customization extends AppCompatActivity {
    public String userName;
    public String themeName;
    public String themeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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

}
