package com.jupiterjam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileMenu extends AppCompatActivity {

    private EditText userName;
    private TextView currentTheme;
    private ImageView themeImage;
    private SharedPreferences userThemePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_menu);

        userThemePrefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);

        userName = findViewById(R.id.usernameInput);
        // Load existing name if it exists
        String savedName = userThemePrefs.getString("userName", "");
        userName.setText(savedName);

        // Save the name when focus is lost
        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String name = userName.getText().toString().trim();
                    userThemePrefs.edit().putString("userName", name).apply();
                }
            }
        });

        // Button and listener to change to theme menu
        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setOnClickListener(v -> {
            setContentView(R.layout.theme_menu);
            setupThemeSelection();
        });
    }

    private void setupThemeSelection() {
        themeImage = findViewById(R.id.ship);

        // Set up listeners to change the users desired rocket theme based on what button is pressed
        findViewById(R.id.themeButton1).setOnClickListener(v -> changeTheme(R.drawable.jupiter_ship, "Jupiter"));
        findViewById(R.id.themeButton2).setOnClickListener(v -> changeTheme(R.drawable.earth_ship, "Earth"));
        findViewById(R.id.themeButton3).setOnClickListener(v -> changeTheme(R.drawable.mars_ship, "Mars"));
        findViewById(R.id.themeButton4).setOnClickListener(v -> changeTheme(R.drawable.neptune_ship, "Neptune"));
        findViewById(R.id.themeButton5).setOnClickListener(v -> changeTheme(R.drawable.saturn_ship, "Saturn"));
        findViewById(R.id.themeButton6).setOnClickListener(v -> changeTheme(R.drawable.moon_ship, "Moon"));
    }

    private void changeTheme(int themeDrawable, String name) {
        // Update theme preview
        themeImage.setImageResource(themeDrawable);

        // Display updated theme name
        currentTheme.setText(name);

        // Save to users SharedPreferences
        SharedPreferences.Editor editor = userThemePrefs.edit();
        editor.putInt("selectedTheme", themeDrawable);
        editor.apply();
    }

    public void revert(View myView){
        startActivity(new Intent(this,MainMenu.class));
        finish();
    }

    public void backToProfile(View myView) {
        setContentView(R.layout.profile_menu);
    }

    /*public int getWins(Player user){
        return user.wins;
    }

    public int getLoses(Player user){
        return user.loses;
    }*/
}
