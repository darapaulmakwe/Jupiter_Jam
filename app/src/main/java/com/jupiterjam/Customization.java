package com.jupiterjam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Customization extends AppCompatActivity {
    private string name;
    private string theme;
    private int wins;
    private int loses;

    private Player(string name, int health, string theme, int wins, int loses ){
        this.name = name;
        this.theme = theme;

    }
    private void setName(Player player){

    }

    public void setTheme(string theme){
        this.theme = theme;
    }
}
